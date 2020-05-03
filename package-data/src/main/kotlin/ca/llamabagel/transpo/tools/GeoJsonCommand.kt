package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.dao.impl.OcTranspoGtfsDirectory
import ca.llamabagel.transpo.models.LatLng
import ca.llamabagel.transpo.tools.util.unzipFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.nio.file.Files

class GeoJsonCommand : CliktCommand(
    name = "geojson"
) {
    private val gtfsZip by argument(help = "The OC Transpo GTFS zip file.").file(
        exists = true,
        readable = true,
        folderOkay = false
    )
    private val tempDir = Files.createTempDirectory("package-data")


    override fun run() {
        unzipFile(gtfsZip, tempDir.toFile())
        val directory = OcTranspoGtfsDirectory(tempDir)


        val trips = directory.trips.getAll().groupBy { it.shapeId }
        val routeShapes = trips.map { (shapeId, trips) ->
            val shapePoints =
                directory.shapes.getById(shapeId!!).sortedBy { it.sequence }.map { LatLng(it.latitude, it.longitude) }
                    .toList()

            val route = directory.routes.getById(trips[0].routeId)
            val coordinates =
                shapePoints.joinToString(separator = ",") { (latitude, longitude) -> "[$longitude, $latitude]" }

            val type = when (route?.shortName?.toIntOrNull()) {
                1 -> "confederation"
                2 -> "trillium"
                6, 7, in 10..12, 14, 40, 44, 80, 85, 90, 88, 111 -> "frequent"
                39, 45, in 61..63, in 97..99 -> "rapid"
                in 200..299 -> "connexion"
                else -> "local"
            }

            """
                {
                    "type": "Feature",
                    "properties": { "type": "$type", "route": "${route?.shortName}", "direction": ${trips[0].directionId} },
                    "geometry": {
                        "type": "LineString",
                        "coordinates": [
                            $coordinates
                        ]
                    }
                }
            """.trimIndent()
        }.joinToString(separator = ",")

        val stops = directory.stops.getAll().joinToString(separator = ",") { stop ->
            """
                {
                    "type": "Feature",
                    "properties": { "id": "${stop.id.value}", "name": "${stop.name}", "code": "${stop.code}" },
                    "geometry": {
                        "type": "Point",
                        "coordinates": [${stop.longitude}, ${stop.latitude}]
                    }
                }
            """.trimIndent()
        }

        val data = """
            {
                "type": "FeatureCollection",
                "features": [
                    $routeShapes,
                    $stops
                ]  
            }
        """.trimIndent()

        File("shapes.json").writeText(data)
    }
}