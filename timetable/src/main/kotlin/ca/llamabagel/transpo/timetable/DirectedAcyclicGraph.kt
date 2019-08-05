package ca.llamabagel.transpo.timetable

import ca.llamabagel.transpo.models.gtfs.StopTime
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph

fun buildRouteGraph(trips: List<List<StopTime>>) {
    val graph = GraphBuilder.directed().allowsSelfLoops(false).build<DataTrip>()
    val starts = mutableSetOf<DataTrip>()
    val ends = mutableSetOf<DataTrip>()

    trips.forEach { trip ->
        starts.add(trip.first().asDataTrip())
        ends.add(trip.last().asDataTrip())

        trip.forEach { stop ->
            if (!graph.addNode(stop.asDataTrip())) {

            }
        }
    }
}