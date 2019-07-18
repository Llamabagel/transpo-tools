package ca.llamabagel.transpo.tools.pack

import ca.llamabagel.transpo.dao.gtfs.GtfsSource
import ca.llamabagel.transpo.dao.listAll
import ca.llamabagel.transpo.models.app.Data
import ca.llamabagel.transpo.models.app.DataPackage
import ca.llamabagel.transpo.models.transit.Route
import ca.llamabagel.transpo.models.transit.Stop
import ca.llamabagel.transpo.models.transit.StopRoute
import ca.llamabagel.transpo.tools.SCHEMA_VERSION
import ca.llamabagel.transpo.tools.pack.transformers.AppRoutesTransformer
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class DataPackager(private val source: GtfsSource, private val originalZip: File, private val revision: Int = 0) {

    fun packageData(): DataPackage {
        val generationDate = Date()
        val dateString = SimpleDateFormat("YYYYMMdd").format(generationDate)
        val version = if (revision > 0) {
            "$dateString-$revision"
        } else {
            dateString
        }

        // Convert GTFS data to App Data format
        val stops = source.stops.listAll().map {
            Stop(
                it.id.value,
                it.code!!,
                it.name,
                it.latitude,
                it.longitude,
                it.locationType!!,
                it.parentStation?.value
            )
        }

        val rawRoutes = source.routes.listAll().map { Route(it.id.value, it.shortName, it.longName, it.type, "", "") }
        val routes = AppRoutesTransformer.transform(rawRoutes)

        val stopRoutes = getStopRoutes().toList()
        // TODO: Shapes data to be generated

        return DataPackage(version, SCHEMA_VERSION, generationDate, Data(stops, routes, stopRoutes, emptyList()))
    }

    private fun getStopRoutes(): Set<StopRoute> {

        val trips = source.trips.getAll()
        val stopTimes = source.stopTimes.getAll()

        return stopTimes
            .asSequence()
            .mapNotNull { stopTime ->
                val associatedTrip = trips.firstOrNull { it.tripId == stopTime.tripId } ?: return@mapNotNull null
                return@mapNotNull stopTime to associatedTrip
            }
            .map { (stopTime, trip) -> StopRoute(stopTime.stopId.value, trip.routeId.value, trip.directionId!!, 0) }
            .toSet()
    }
}
