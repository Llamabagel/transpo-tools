package ca.llamabagel.transpo.timetable

import ca.llamabagel.transpo.models.gtfs.StopTime
import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import org.jgrapht.traverse.DepthFirstIterator

data class GraphResults(
    val graph: Graph<DataTrip>,
    val uniqueTripPaths: Set<List<DataTrip>> = emptySet(),
    val timetableOrdering: List<DataTrip> = emptyList()
)

fun buildRouteGraph(trips: List<List<StopTime>>): GraphResults {
    val graph = GraphBuilder.directed().allowsSelfLoops(false).build<DataTrip>()
    val startEnds = mutableSetOf<Pair<DataTrip, DataTrip>>()
    // Partial paths are guaranteed to contain the start and the end of each path but may also include partial
    // segments of the path in the middle to identify unique paths
    val partialPaths = mutableSetOf<PartialPath>()


    trips.forEach { trip ->
        val stops = trip.map(StopTime::asDataTrip)
        val path = mutableListOf<DataTrip>()
        startEnds.add(stops.first() to stops.last())

        stops.forEachIndexed { index, stop ->
            if (!graph.addNode(stop)) {
                if (index > 0) {
                    graph.putEdge(stops[index - 1], stop)
                }
            } else {
                path.add(stop)
            }

            if (index > 0 && !graph.hasEdgeConnecting(stops[index - 1], stop)) {
                graph.putEdge(stops[index - 1], stop)
            }
        }

        partialPaths.add(PartialPath(stops.first(), stops.last(), path))
    }

    val uniquePaths = partialPaths.flatMap { (start, end, through) ->
        graph.findAllPathsBetween(start, end, through.toMutableSet())
    }.toSet()

    println("paths: $partialPaths")

    return GraphResults(graph)
}

fun <T> Graph<T>.findAllPathsBetween(u: T, v: T, through: MutableSet<T> = mutableSetOf()): Set<List<T>> {

    return recursiveFindAllPathsBetween(u, v)
}

private fun <T> Graph<T>.recursiveFindAllPathsBetween(
    u: T,
    v: T,
    through: MutableSet<T> = mutableSetOf(),
    visited: MutableSet<T> = mutableSetOf(),
    path: MutableList<T> = mutableListOf()
): Set<List<T>> {
    visited.add(u)
    var removed = false
    if (through.contains(u)) {
        through.remove(u)
        removed = true
    }

    if (u == v && through.isEmpty()) {
        // Return a copy of the path list so that it can be modified in other recursive calls
        return setOf(mutableListOf<T>().apply { addAll(path) }).also { visited.remove(u) }
    }

    return adjacentNodes(u)
        .filter { hasEdgeConnecting(u, it) && !visited.contains(it) }
        .flatMap { adjacentNode ->
            path.add(adjacentNode)
            recursiveFindAllPathsBetween(adjacentNode, v, through, visited, path).also {
                path.removeAt(path.size - 1)
            }
        }
        .toSet()
        .also {
            visited.remove(u)
            if (removed) {
                through.add(u)
            }
        }
}