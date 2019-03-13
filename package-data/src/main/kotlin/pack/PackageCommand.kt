package pack

import ca.llamabagel.transpo.dao.impl.GtfsDirectory
import ca.llamabagel.transpo.dao.impl.OcTranspoGtfsDirectory
import ca.llamabagel.transpo.models.app.Stop
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.util.zip.ZipFile

class PackageCommand : CliktCommand(name = "package", help = "Package App data from an OC Transpo GTFS zip file. A version number will be automatically generated for the package.") {
    private val gtfsZip by argument().file(exists = true, readable = true, folderOkay = false)
    private val revision by option(help = "The revision number to be included in the version number.")

    override fun run() {
        // Unzips the given OC Transpo GTFS zip for processing.
        unzipGtfs()

        copyData()

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

        // Copy all gtfs values over to raw gtfs
        println("Copying stops")
        gtfs.stops.insert(*ocSource.stops.getAll().toTypedArray())

        println("Copying routes")
        gtfs.routes.insert(*ocSource.routes.getAll().toTypedArray())

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

        val convertedStops = gtfs.stops.getAll().map { Stop(it.id.value, it.code ?: "", it.name, it.latitude, it.longitude, it.locationType ?: 0, it.parentStation) }
    }

    private fun cleanup() {
        // Delete the unzipped gtfs data
        File("gtfs").deleteRecursively()

        File("rawGtfs").deleteRecursively()
    }

}