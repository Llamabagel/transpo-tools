package ca.llamabagel.transpo.tools.octranspo

import ca.llamabagel.transpo.dao.gtfs.GtfsSource
import ca.llamabagel.transpo.models.gtfs.Route
import ca.llamabagel.transpo.models.gtfs.RouteId
import ca.llamabagel.transpo.tools.DataTransformer

class RoutesTransformer(source: GtfsSource) : DataTransformer<Route>() {

    private val headsigns: Map<RouteId, Array<String?>>

    init {
        val headsignCounts: Map<RouteId, Array<MutableMap<String, Int>>> = source.routes.getAll().map {
            it.id to Array(2) { mutableMapOf<String, Int>() }
        }.toMap()

        // Get the frequencies of all headers for each route
        val trips = source.trips.getAll()
        trips.forEach {
            if (headsignCounts.getValue(it.routeId)[it.directionId!!][it.headsign] != null) {
                headsignCounts.getValue(it.routeId)[it.directionId!!][it.headsign!!]?.inc()
            } else {
                headsignCounts.getValue(it.routeId)[it.directionId!!][it.headsign!!] = 1
            }
        }

        // Pick the max occurring headsign to use for display purposes
        headsigns = headsignCounts.map { (routeId, data) ->
            val from = data[0].maxBy { it.value }?.key
            val to = data[1].maxBy { it.value }?.key

            routeId to arrayOf(from, to)
        }.toMap()
    }

    override fun removeItem(item: Route): Boolean = false

    override fun mapItem(item: Route): Route {
        val sign = headsigns.getValue(item.id)
        val copy = item.copy(longName = "${sign[0] ?: ""} \\ ${sign[1] ?: ""}")

        println("Mapped $item to $copy")
        return copy
    }

    override fun injectItems(): List<Route> = emptyList()
}