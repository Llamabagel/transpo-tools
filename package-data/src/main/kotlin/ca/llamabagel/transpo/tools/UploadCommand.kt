package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.Configuration
import ca.llamabagel.transpo.dao.impl.GtfsDatabase
import ca.llamabagel.transpo.dao.impl.GtfsDirectory
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Paths
import java.sql.Connection
import java.util.zip.ZipFile

class UploadCommand : CliktCommand(
    name = "upload",
    help = "Uploads a specified data package version to the server.",
    epilog = "This will copy the generated .zip file created by the package command to the server specified in configuration file and copy all data to the SQL server.\n" +
            "\n" +
            "This command **must** be run on the target machine. The zip files are copied directly through the filesystem."
) {
    private val version: String by argument(
        "version",
        help = "The version number of the data package to upload. Will look for `<version>.zip` as the package."
    )

    private val dbConnection: Connection by lazy { Configuration.getConnection() ?: throw IllegalStateException() }
    private val gtfsDatabase: GtfsDatabase by lazy { GtfsDatabase(dbConnection) }

    override fun run() {
        val file = File("$version.zip")

        if (!file.exists()) {
            println("Could not find data package for version: $version")
            return
        }

        unzipPackage(file)
        uploadGtfsData()
    }

    private fun unzipPackage(zipFile: File) {
        val directoryPath = "${Configuration.DATA_PACKAGE_DIRECTORY}/$SCHEMA_VERSION/$version"
        // Create the destination directory for the package files
        File(directoryPath).mkdir()

        // Unzip the package zip file
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    File("$directoryPath/${entry.name}").outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    private fun uploadGtfsData() {
        val directoryPath = "${Configuration.DATA_PACKAGE_DIRECTORY}/$SCHEMA_VERSION/$version"

        // Unzip raw GTFS data
        val temporaryDirectory = File("$directoryPath/rawGtfs").apply { mkdir() }
        ZipFile(File("$directoryPath/rawGtfs.zip")).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    File("$directoryPath/rawGtfs/${entry.name}").outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }

        val gtfsData = GtfsDirectory(Paths.get("$directoryPath/rawGtfs"))

        // Clear all data and insert all new data
        val statement = dbConnection.createStatement()
        statement.execute(
            """
            DELETE FROM stop_times;
            DELETE FROM trips;
            DELETE FROM shapes;
            DELETE FROM calendar_dates;
            DELETE FROM calendars;
            DELETE FROM stops;
            DELETE FROM routes;
            DELETE FROM agencies;
        """.trimIndent()
        )

        gtfsDatabase.agencies.insert(*gtfsData.agencies.getAll().toTypedArray())
        gtfsDatabase.stops.insert(*gtfsData.stops.getAll().toTypedArray())
        gtfsDatabase.routes.insert(*gtfsData.routes.getAll().toTypedArray())
        gtfsDatabase.calendars.insert(*gtfsData.calendars.getAll().toTypedArray())
        gtfsDatabase.calendarDates.insert(*gtfsData.calendarDates.getAll().toTypedArray())
        gtfsData.shapes?.getAll()?.toTypedArray()?.let { gtfsDatabase.shapes?.insert(*it) }
        gtfsDatabase.trips.insert(*gtfsData.trips.getAll().toTypedArray())
        gtfsDatabase.stopTimes.insert(*gtfsData.stopTimes.getAll().toTypedArray())

        // Insert metadata
        statement.execute("INSERT INTO data_versions (version, schema_version) VALUES ($version, $SCHEMA_VERSION)")
        statement.execute("INSERT INTO metadata VALUES ('android', '$version', $SCHEMA_VERSION, now(), 1) ON CONFLICT (platform) DO UPDATE SET data_version = $version, schema_version = $SCHEMA_VERSION")

        // Clean up temporary files
        temporaryDirectory.deleteRecursively()
    }
}