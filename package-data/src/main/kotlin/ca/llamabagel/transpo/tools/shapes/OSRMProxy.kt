package ca.llamabagel.transpo.tools.shapes

import ca.llamabagel.transpo.models.gtfs.Stop
import ca.llamabagel.transpo.tools.Config
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OSRMProxy(private val config: Config) {
    private val osrmService = Retrofit.Builder()
        .baseUrl("http://${config.osrm.host}:${config.osrm.port}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OSRMService::class.java)

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Plots a route based on a sequence of [Stop]s.
     * @return An encoded Polyline, or null if no route was found
     */
    suspend fun getRouteShape(stops: List<Stop>): String? {
        val coordinates = stops.joinToString(separator = ";") { stop -> "${stop.longitude},${stop.latitude}" }
        val result = osrmService.route(coordinates)

        logger.info(result.routes[0].geometry)

        return result.routes[0].geometry
    }
}