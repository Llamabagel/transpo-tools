package ca.llamabagel.transpo.tools.pack

import ca.llamabagel.transpo.dao.impl.GtfsDirectory
import ca.llamabagel.transpo.dao.impl.OcTranspoGtfsDirectory
import ca.llamabagel.transpo.models.app.Data
import ca.llamabagel.transpo.models.app.DataPackage
import ca.llamabagel.transpo.models.transit.Route
import ca.llamabagel.transpo.models.transit.Stop
import ca.llamabagel.transpo.tools.SCHEMA_VERSION
import ca.llamabagel.transpo.tools.pack.transformers.RoutesTransformer
import ca.llamabagel.transpo.tools.pack.transformers.StopsTransformer
import ca.llamabagel.transpo.tools.util.zipFiles
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipFile

class PackageCommand : CliktCommand(
    name = "package",
    help = "Package App data from an OC Transpo GTFS zip file.",
    epilog = "Packages a GTFS file into a data package that can be uploaded to the server using the `upload` command. \n" +
            "This command converts the GTFS data into both the set of data that will be used by individual devices, as well as a full GTFS dataset which is uploaded directly into the server.\n" +
            "\n" +
            "This command outputs a .zip file with the version name of the data package."
) {
    private val gtfsZip by argument(help = "The OC Transpo GTFS zip file.").file(
        exists = true,
        readable = true,
        folderOkay = false
    )
    private val revision by option(help = "The revision number to be included in the version number.")

    override fun run() {
        // Unzips the given OC Transpo GTFS zip for processing.
        unzipGtfs()
        copyData()

        // ShapesDownloader(GtfsDirectory(File("rawGtfs").toPath()))

        packageData()

        cleanup()
    }

    private fun unzipGtfs() {
        // Create the gtfs directory
        File("gtfs").mkdir()

        // Unzip the given zip file
        ZipFile(gtfsZip).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    File("gtfs/${entry.name}").outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    private fun copyData() {
        val ocSource = OcTranspoGtfsDirectory(File("gtfs").toPath())
        File("rawGtfs").mkdir()
        val gtfs = GtfsDirectory(File("rawGtfs").toPath())

        // Copy all gtfs values over to raw gtfs and transform the data
        println("Copying stops")
        val transformedStops = StopsTransformer.transform(ocSource.stops.getAll())
        gtfs.stops.insert(*transformedStops.toTypedArray())

        println("Copying routes")
        val transformedRoutes = RoutesTransformer(ocSource).transform(ocSource.routes.getAll())
        gtfs.routes.insert(*transformedRoutes.toTypedArray())

        println("Copying agency")
        gtfs.agencies.insert(*ocSource.agencies.getAll().toTypedArray())

        println("Copying calendars")
        gtfs.calendars.insert(*ocSource.calendars.getAll().toTypedArray())

        println("Copying calendar dates")
        gtfs.calendarDates.insert(*ocSource.calendarDates.getAll().toTypedArray())

        println("Copying stop times")
        gtfs.stopTimes.insert(*ocSource.stopTimes.getAll().toTypedArray())

        println("Copying trips")
        gtfs.trips.insert(*ocSource.trips.getAll().toTypedArray())

        println("Done copying")
    }

    private fun packageData() {
        val gtfs = GtfsDirectory(File("rawGtfs").toPath())

        val convertedStops = gtfs.stops.getAll().map {
            Stop(
                it.id.value,
                it.code ?: "",
                it.name,
                it.latitude,
                it.longitude,
                it.locationType ?: 0,
                it.parentStation?.value
            )
        }
        // TODO: Long names and Service Levels
        val convertedRoutes = gtfs.routes.getAll().map { Route(it.id.value, it.shortName, "", it.type, "", "") }

        // Create the data package object
        val version = SimpleDateFormat("YYYYMMdd").format(Date()) + (revision ?: "")
        val dataPackage = DataPackage(
            version,
            SCHEMA_VERSION,
            Date(),
            Data(convertedStops, convertedRoutes, emptyList(), emptyList())
        )
        // Write data package to a json file
        FileWriter("$version.json").use {
            it.write(Json.stringify(DataPackage.serializer(), dataPackage))
        }

        // Zip raw GTFS files
        zipFiles(
            "RawGTFS.zip",
            "rawGtfs/stops.txt" to "stops.txt",
            "rawGtfs/routes.txt" to "routes.txt",
            "rawGtfs/agency.txt" to "agency.txt",
            "rawGtfs/calendar.txt" to "calendar.txt",
            "rawGtfs/calendar_dates.txt" to "calendar_dates.txt",
            "rawGtfs/stop_times.txt" to "stop_times.txt",
            "rawGtfs/trips.txt" to "trips.txt",
            "rawGtfs/shapes.txt" to "shapes.txt"
        )

        // Zip all files required for the package
        zipFiles("$version.zip",
            "RawGTFS.zip" to "RawGTFS.zip",
            "$version.json" to "$version.json",
            gtfsZip.absolutePath to "GTFS.zip")

        // Delete the temporarily created RawGTFS zip file
        File("RawGTFS.zip").delete()
        File("$version.json").delete()
    }

    /**
     * Cleanup temporary files
     */
    private fun cleanup() {
        File("gtfs").deleteRecursively()
        File("rawGtfs").deleteRecursively()
    }
}