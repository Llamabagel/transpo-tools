package ca.llamabagel.transpo.tools.graph

import ca.llamabagel.transpo.dao.gtfs.GtfsSource
import ca.llamabagel.transpo.models.gtfs.RouteId
import ca.llamabagel.transpo.models.gtfs.StopId
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.traverse.TopologicalOrderIterator

class RouteGraphBuilder(val routeId: RouteId, val directionId: Int, private val source: GtfsSource) {
    private val graph: DirectedAcyclicGraph<StopId, DefaultEdge> = DirectedAcyclicGraph(DefaultEdge::class.java)

    fun buildGraph() {
        val trips = getAllTripsAsStops()

        trips.forEach { trip ->
            trip.forEachIndexed { index, stop ->
                if (!graph.addVertex(stop)) {
                    if (index > 0) {
                        try {
                            graph.addEdge(trip[index - 1], stop)
                        } catch (e: Exception) {
                            graph.addVertex(StopId("$stop-2"))
                            graph.addEdge(trip[index - 1], StopId("$stop-2"))
                            println("Could not add edge between ${trip[index - 1]} and $stop")
                        }
                    }
                }

                if (index > 0 && !graph.containsEdge(trip[index - 1], stop)) {
                    try {
                        graph.addEdge(trip[index - 1], stop)
                    } catch (e: Exception) {
                        graph.addVertex(StopId("$stop-2"))
                        graph.addEdge(trip[index - 1], StopId("$stop-2"))
                        println("Could not add edge between ${trip[index - 1]} and $stop")
                    }
                }
            }
        }
    }

    /**
     * Returns an ordered list of all the stops in the route.
     * This list will be a "flattened" version of the generated graph.
     */
    fun getFlatStopOrder(): List<StopId> {
        val iterator = TopologicalOrderIterator<StopId, DefaultEdge>(graph)
        val order = mutableListOf<StopId>()
        iterator.forEach { order.add(it) }

        return order
    }

    private fun getAllTripsAsStops(): List<List<StopId>> {
        return source.trips.getByRouteId(routeId, directionId).map { trip ->
            source.stopTimes.getByTripId(trip.tripId).sortedBy { it.stopSequence }.map { it.stopId }.toList()
        }.toList()
    }
}