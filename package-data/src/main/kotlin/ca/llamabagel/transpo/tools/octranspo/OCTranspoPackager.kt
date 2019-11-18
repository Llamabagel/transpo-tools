package ca.llamabagel.transpo.tools.octranspo

import ca.llamabagel.transpo.dao.gtfs.GtfsSource
import ca.llamabagel.transpo.dao.listAll
import ca.llamabagel.transpo.models.gtfs.RouteId
import ca.llamabagel.transpo.models.gtfs.Shape
import ca.llamabagel.transpo.models.gtfs.ShapeId
import ca.llamabagel.transpo.models.gtfs.StopId
import ca.llamabagel.transpo.models.gtfs.TripId
import ca.llamabagel.transpo.models.gtfs.asTripId
import ca.llamabagel.transpo.models.transit.RouteShape
import ca.llamabagel.transpo.models.transit.StopRoute
import ca.llamabagel.transpo.tools.AgencyPackager
import ca.llamabagel.transpo.tools.Config
import ca.llamabagel.transpo.tools.TransitRoute
import ca.llamabagel.transpo.tools.TransitStop
import ca.llamabagel.transpo.tools.shapes.OSRMProxy
import ca.llamabagel.transpo.tools.util.decode
import ca.llamabagel.transpo.tools.util.pmap
import kotlinx.coroutines.runBlocking
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.traverse.TopologicalOrderIterator
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class OCTranspoPackager(private val gtfsIn: GtfsSource, private val config: Config) : AgencyPackager(gtfsIn, config) {
    private val logger = LoggerFactory.getLogger(AgencyPackager::class.java)
    private val uniqueTrips: MutableMap<Pair<RouteId, Int>, Set<TripId>> = mutableMapOf()

    override fun doPackage() {
        logger.info("--- Packaging GTFS data ---")
        logger.info("Packaging Stops")
        packageStops(StopsTransformer.transform(gtfsIn.stops.listAll()))
        logger.info("Packaging Routes")
        packageRoutes(RoutesTransformer(gtfsIn).transform(gtfsIn.routes.listAll()))
        logger.info("Packaging Calendars")
        packageCalendars(gtfsIn.calendars.listAll())
        logger.info("Packaging Calendar Dates")
        packageCalendarDates(gtfsIn.calendarDates.listAll())
        logger.info("Packaging Stop Times")
        packageStopTimes(gtfsIn.stopTimes.listAll())
        logger.info("Packaging Trips")
        packageTrips(gtfsIn.trips.listAll())

        logger.info("--- Calculating Unique Trips ---")
        calculateUniqueTrips()

        logger.info("Packaging Shapes")
        gtfsIn.shapes?.let { shapes ->
            packageShapes(shapes.listAll())
        }
        logger.info("--- Done packaging GTFS data ---\n")

        logger.info("--- Packaging Transit data ---")
        logger.info("Packaging Stops")
        packageTransitStops(packaging.stops.listAll().map { (id, code, name, _, latitude, longitude, _, _, locationType, parentStation) ->
            TransitStop(
                id.value,
                code ?: "",
                name,
                latitude,
                longitude,
                locationType ?: 0,
                parentStation?.value
            )
        })
        logger.info("Packaging Routes")
        packageTransitRoutes(AppRoutesTransformer.transform(packaging.routes.listAll().map { (id, _, shortName, longName, _, type) ->
            TransitRoute(
                id.value,
                shortName,
                longName,
                type,
                "",
                ""
            )
        }))
        logger.info("Packaging Stop Routes")
        val stopRoutes = uniqueTrips.entries.flatMap { (key, value) ->
            generateStopRouteSequence(key.first, key.second, value)
        }
        packageTransitStopRoutes(stopRoutes)

        val proxy = OSRMProxy(config)
        logger.info("Calculating and Packaging Shapes")
        val stopLists = uniqueTrips.entries.mapNotNull { (key, trips) ->
            if (trips.isNotEmpty()) {
                trips.first() to packaging.stopTimes.getByTripId(trips.first()).map { packaging.stops.getById(it.stopId)!! }.toList()
            } else {
                logger.warn("Could not find trips for route $key")
                null
            }
        }.toMap()

        val shapes = mutableListOf<Pair<TripId, String?>>()
        runBlocking {
            shapes.addAll(stopLists.entries.pmap { (key, value) -> key to proxy.getRouteShape(value) })
        }

        val atomic = AtomicInteger()
        val shapeObjects = shapes.mapNotNull { (tripId, polyline) ->
            if (polyline != null) {
                val decoded = decode(polyline, 6)
                val trip = packaging.trips.getByTripId(tripId)!!

                val unique = atomic.getAndIncrement()

                return@mapNotNull RouteShape(
                    trip.routeId.value,
                    "${trip.routeId.value}-$unique",
                    polyline
                ) to decoded.mapIndexed { index, (latitude, longitude) ->
                    Shape(ShapeId("${trip.routeId.value}-$unique"), latitude, longitude, index, null)
                }
            }

            return@mapNotNull null
        }
        packageShapes(shapeObjects.map { (_, shapes) -> shapes }.flatten())
        packageTransitRouteShapes(shapeObjects.map { (shape, _) -> shape })

        logger.info("--- Done packaging Transit data ---\n")
    }

    private fun calculateUniqueTrips() {
        gtfsIn.routes.getAll().forEach { (id) ->
            for (i in 0..1) {
                logger.info("Computing Route ${id.value} direction $i")
                calculateUniqueTrips(id, i)
            }
        }
    }

    private fun calculateUniqueTrips(routeId: RouteId, directionId: Int) {
        val statement =
            packagingConnection.prepareStatement("SELECT DISTINCT stop_id, stop_sequence FROM stop_times WHERE trip_id IN (SELECT trip_id FROM trips WHERE route_id = ? AND direction_id = ?) ORDER BY stop_sequence")
                .apply {
                    setString(1, routeId.value)
                    setInt(2, directionId)
                }

        val tripIds = mutableSetOf<TripId>()
        val result = statement.executeQuery()
        val results = generateSequence {
            return@generateSequence if (result.next()) {
                result.getString(1) to result.getInt(2)
            } else {
                null
            }
        }.toList()
        val resultSet = HashSet(results)

        results.forEach { item ->
            if (resultSet.contains(item)) {
                val matchStatement =
                    packagingConnection.prepareStatement("SELECT trip_id FROM stop_times WHERE stop_id = ? AND stop_sequence = ? AND trip_id IN (SELECT trip_id FROM trips WHERE route_id = ? AND direction_id = ?) LIMIT 1")
                        .apply {
                            setString(1, item.first)
                            setInt(2, item.second)
                            setString(3, routeId.value)
                            setInt(4, directionId)
                        }

                val matchResult = matchStatement.executeQuery()
                matchResult.next()
                val tripId = matchResult.getString(1).asTripId()!!

                packaging.stopTimes.getByTripId(tripId)
                    .map { it.stopId.value to it.stopSequence }
                    .forEach {
                        resultSet.remove(it)
                    }
                tripIds.add(tripId)
            }
        }

        uniqueTrips[routeId to directionId] = tripIds
    }

    private fun generateStopRouteSequence(routeId: RouteId, directionId: Int, tripIds: Set<TripId>): List<StopRoute> {
        val graph = DirectedAcyclicGraph<StopId, DefaultEdge>(DefaultEdge::class.java)

        logger.info("Generating sequence for route ${routeId.value} direction $directionId")

        tripIds.forEach { tripId ->
            val trip = packaging.stopTimes.getByTripId(tripId).map { stopTime -> stopTime.stopId }.toList()

            trip.forEachIndexed { index, stop ->
                if (!graph.addVertex(stop)) {
                    if (index > 0) {
                        try {
                            graph.addEdge(trip[index - 1], stop)
                        } catch (e: Exception) {
                            graph.addVertex(StopId("${stop.value}-2"))
                            graph.addEdge(trip[index - 1], StopId("${stop.value}-2"))
                        }
                    }
                }

                if (index > 0 && !graph.containsEdge(trip[index - 1], stop)) {
                    try {
                        graph.addEdge(trip[index - 1], stop)
                    } catch (e: Exception) {
                        graph.addVertex(StopId("${stop.value}-2"))
                        graph.addEdge(trip[index - 1], StopId("${stop.value}-2"))
                    }
                }
            }
        }

        val iterator = TopologicalOrderIterator<StopId, DefaultEdge>(graph)
        val order = mutableListOf<StopId>()
        iterator.forEach { order += it }

        return order.mapIndexed { index, id ->
            return@mapIndexed if (id.value.contains("-")) {
                StopRoute(id.value.split("-")[0], routeId.value, directionId, index)
            } else {
                StopRoute(id.value, routeId.value, directionId, index)
            }
        }
    }
}