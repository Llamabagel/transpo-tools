package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.dao.gtfs.GtfsSource
import ca.llamabagel.transpo.dao.impl.GtfsDatabase
import ca.llamabagel.transpo.dao.listAll
import ca.llamabagel.transpo.models.app.Data
import ca.llamabagel.transpo.models.app.DataPackage
import ca.llamabagel.transpo.models.gtfs.Calendar
import ca.llamabagel.transpo.models.gtfs.CalendarDate
import ca.llamabagel.transpo.models.gtfs.Route
import ca.llamabagel.transpo.models.gtfs.Shape
import ca.llamabagel.transpo.models.gtfs.Stop
import ca.llamabagel.transpo.models.gtfs.StopTime
import ca.llamabagel.transpo.models.gtfs.Trip
import ca.llamabagel.transpo.models.transit.StopRoute
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.util.Date

typealias TransitRoute = ca.llamabagel.transpo.models.transit.Route
typealias TransitStop = ca.llamabagel.transpo.models.transit.Stop

abstract class AgencyPackager(private val gtfsIn: GtfsSource) {

    protected val packagingConnection: Connection by lazy {
        DriverManager.getConnection(
            "jdbc:postgresql://${Configuration.SQL_HOST}:${Configuration.SQL_PORT}/packaging",
            Configuration.SQL_USER,
            Configuration.SQL_PASSWORD
        )
    }

    protected val packaging = GtfsDatabase(packagingConnection)

    private val transitStops = mutableListOf<TransitStop>()
    private val transitRoutes = mutableListOf<TransitRoute>()
    private val transitStopRoutes = mutableListOf<StopRoute>()

    fun packageData(version: String): DataPackage {
        doPackage()

        return DataPackage(version,
            1,
            Date(),
            Data(transitStops, transitRoutes, transitStopRoutes, emptyList())
        )
    }

    abstract fun doPackage()
    
    protected fun packageRoutes(routes: List<Route>) {
        packaging.routes.insert(*routes.toTypedArray())
    }

    protected fun packageStops(stops: List<Stop>) {
        packaging.stops.insert(*stops.toTypedArray())
    }

    protected fun packageCalendars(calendars: List<Calendar>) {
        packaging.calendars.insert(*calendars.toTypedArray())
    }

    protected fun packageCalendarDates(calendarDates: List<CalendarDate>) {
        packaging.calendarDates.insert(*calendarDates.toTypedArray())
    }

    protected fun packageShapes(shapes: List<Shape>) {
        packaging.shapes?.insert(*shapes.toTypedArray())
    }

    protected fun packageStopTimes(stopTimes: List<StopTime>) {
        packaging.stopTimes.insert(*stopTimes.toTypedArray())
    }

    protected fun packageTrips(trips: List<Trip>) {
        packaging.trips.insert(*trips.toTypedArray())
    }

    protected fun packageTransitStops(stops: List<TransitStop>) {
        transitStops.addAll(stops)
    }

    protected fun packageTransitRoutes(routes: List<TransitRoute>) {
        transitRoutes.addAll(routes)
    }

    protected fun packageTransitStopRoutes(stopRoutes: List<StopRoute>) {
        transitStopRoutes.addAll(stopRoutes)
    }
}