package ca.llamabagel.transpo.timetable

import ca.llamabagel.transpo.dao.impl.GtfsDatabase
import ca.llamabagel.transpo.models.gtfs.RouteId
import ca.llamabagel.transpo.models.gtfs.TripId
import ca.llamabagel.transpo.models.gtfs.asTripId
import com.github.ajalt.clikt.core.CliktCommand
import com.google.common.graph.EndpointPair
import com.google.common.graph.ImmutableGraph
import com.google.common.graph.MutableGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.guava.BaseGraphAdapter
import org.jgrapht.graph.guava.ImmutableGraphAdapter
import org.jgrapht.graph.guava.MutableGraphAdapter
import org.jgrapht.io.DOTExporter
import org.jgrapht.io.GraphExporter
import java.sql.Connection
import java.sql.DriverManager
import java.io.StringWriter
import java.io.Writer
import org.jgrapht.io.ComponentNameProvider
import org.jgrapht.traverse.TopologicalOrderIterator
import java.lang.StringBuilder
import kotlin.system.measureTimeMillis


val Configuration = ca.llamabagel.transpo.Configuration("./")
private val dataConnection: Connection by lazy {
    DriverManager.getConnection(
        "jdbc:postgresql://${Configuration.SQL_HOST}:${Configuration.SQL_PORT}/packaging",
        Configuration.SQL_USER,
        Configuration.SQL_PASSWORD
    )
}


fun main() {
    /*val gtfs = GtfsDatabase(dataConnection)

    val id = readLine().toString()
    val direction = readLine()!!.toInt()

    val trips = gtfs.trips.getByRouteId(RouteId(id), direction)

    val times = trips.map { trip ->
        gtfs.stopTimes.getByTripId(trip.tripId).toList().sortedBy { it.stopSequence }
    }.toList()

    val graphData = buildRouteGraph(times.toList())
    val adapter = MutableGraphAdapter(graphData.graph as MutableGraph)

    val vertexIdProvider = ComponentNameProvider<DataTrip> { trip -> trip.stopId }
    val vertexLabelProvider = ComponentNameProvider<DataTrip> { trip -> trip.stopId }
    //val edgeLabelProvider = ComponentNameProvider<EndpointPair<DataTrip>> { pair -> "${pair.nodeV().sequence}" }

    val exporter: GraphExporter<DataTrip, EndpointPair<DataTrip>> =
        DOTExporter(vertexIdProvider, vertexLabelProvider, null)

    val iterator = TopologicalOrderIterator<DataTrip, EndpointPair<DataTrip>>(adapter)
    val sb = StringBuilder()

    iterator.forEach {
        if (sb.isNotEmpty()) sb.append(" > ")
        sb.append(it.stopId)
    }

    println("Flattened: ")
    println(sb.toString())

    val writer = StringWriter()
    exporter.exportGraph(adapter, writer)
    println(writer.toString())
    println()

    graphData.uniqueTripPaths.forEach { order ->
        println(order)
    }*/

    val t1 = measureTimeMillis {
        val result = getUniqueTrips("44-301", 0)
        println("44-0: ${result.size}")
    }
    println("44-0 took ${t1}ms")

    val t2 = measureTimeMillis {
        val result = getUniqueTrips("44-301", 1)
        println("44-1: ${result.size}")
    }
    println("44-1 took ${t2}ms")

    val t3 = measureTimeMillis {
        val result = getUniqueTrips("61-301", 1)
        println("61-1: ${result.size}")
    }
    println("61-1 took ${t3}ms")

    val t4 = measureTimeMillis {
        val result = getUniqueTrips("99-301", 1)
        println("99-1: ${result.size}")
    }
    println("99-1 took ${t4}ms")
}

fun getUniqueTrips(id: String, direction: Int): Set<String> {
    val gtfs = GtfsDatabase(dataConnection)
    val statement =
        dataConnection.prepareStatement("SELECT DISTINCT stop_id, stop_sequence FROM stop_times WHERE trip_id IN (SELECT trip_id FROM trips WHERE route_id = ? AND direction_id = ?) ORDER BY stop_sequence")
            .apply {
                setString(1, id)
                setInt(2, direction)
            }

    val tripIds = mutableSetOf<String>()
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
                dataConnection.prepareStatement("SELECT trip_id FROM stop_times WHERE stop_id = ? AND stop_sequence = ? AND trip_id IN (SELECT trip_id FROM trips WHERE route_id = ? AND direction_id = ?) LIMIT 1")
                    .apply {
                        setString(1, item.first)
                        setInt(2, item.second)
                        setString(3, id)
                        setInt(4, direction)
                    }

            val matchResult = matchStatement.executeQuery()
            matchResult.next()
            val tripId = matchResult.getString(1)

            gtfs.stopTimes.getByTripId(tripId.asTripId()!!)
                .map { it.stopId.value to it.stopSequence }
                .forEach {
                    resultSet.remove(it)
                }
            tripIds.add(tripId)
        }
    }

    return tripIds
}

/*
class TimetableCommand : CliktCommand() {

}*/
