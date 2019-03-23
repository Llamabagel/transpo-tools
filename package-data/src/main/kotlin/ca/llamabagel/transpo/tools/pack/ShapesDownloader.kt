package ca.llamabagel.transpo.tools.pack

import ca.llamabagel.transpo.dao.gtfs.GtfsSource
import ca.llamabagel.transpo.models.gtfs.Route
import ca.llamabagel.transpo.models.gtfs.RouteId
import ca.llamabagel.transpo.models.gtfs.Trip
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.system.measureTimeMillis

class ShapesDownloader(private val gtfs: GtfsSource) {
    val date = "2019-3-11"

    private val uniqueTrips: MutableMap<RouteId, Array<MutableList<UniqueTrip>>> = mutableMapOf()
    private val stopTimes = gtfs.stopTimes.getAll()

    init {
        val routes = gtfs.routes.getAll();
        routes.forEach {
            uniqueTrips[it.id] = Array(2) { mutableListOf<UniqueTrip>() }
        }

        println("Finding unique trips")
        val time = measureTimeMillis {
            runBlocking { findUniqueTrips() }
        }
        println("Found unique trips in $time ms")
    }

    private suspend fun findUniqueTrips() {
        fun findUniqueTripsForRouteAndDirection(route: Route, directionId: Int) {
            val trips = gtfs.trips.getByRouteId(route.id, directionId)

            trips.forEach { trip ->
                var hasMatch = false
                // Goes through each unique trip on this route/direction to check if there are any that match the current trip
                uniqueTrips[route.id]!![directionId].forEach unique@ {
                    if (trip matches it.unique) {
                        it.similar.add(trip)
                        hasMatch = true
                        return@unique
                    }
                }

                // If no matching trip was found, we have a new unique trip!
                if (!hasMatch) {
                    uniqueTrips[route.id]!![directionId].add(UniqueTrip(route, trip, mutableListOf(), Date()))
                    println("UNIQUE: ${trip.routeId.value} ${trip.tripId.value}")
                } else {
                    println("NON-UNIQUE: ${trip.routeId.value} ${trip.tripId.value}")
                }
            }
        }

        val routes = gtfs.routes.getAll()

        routes.map {route ->
            GlobalScope.launch {
                println("START Route ${route.shortName}")
                findUniqueTripsForRouteAndDirection(route, 0)
                findUniqueTripsForRouteAndDirection(route, 1)
                println("END Route ${route.shortName}")
            }
        }.forEach {
            it.join()
        }
    }


    private suspend fun getRouteMapData(routeNumber: String, directionId: Int) {

    }

    private infix fun Trip.matches(other: Trip): Boolean {
        val thisTimes = stopTimes.filter { it.tripId == this.tripId }
        val otherTimes = stopTimes.filter { it.tripId == other.tripId }

        thisTimes.forEachIndexed { index, stopTime ->
            if (index < otherTimes.size && otherTimes[index].stopId != stopTime.stopId) {
                return false
            }
        }

        return true
    }

}

data class UniqueTrip(val route: Route, val unique: Trip, val similar: MutableList<Trip>, val date: Date)
