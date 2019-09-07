package ca.llamabagel.transpo.tools.pack

import ca.llamabagel.transpo.dao.impl.OcTranspoGtfsDirectory
import ca.llamabagel.transpo.models.app.DataPackage
import ca.llamabagel.transpo.tools.Configuration
import ca.llamabagel.transpo.tools.octranspo.OCTranspoPackager
import ca.llamabagel.transpo.tools.util.zipFiles
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.serialization.json.Json
import org.flywaydb.core.Flyway
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
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
    private val tempDir = Files.createTempDirectory("package-data")

    override fun run() {
        TermUi.confirm("This command will clean and use a database named \"packaging\" on your local postgres instance. Continue?", abort = true)

        configurePackagingDatabase()

        // Unzips the given OC Transpo GTFS zip for processing.
        unzipGtfs()
        //copyData()

        // ShapesDownloader(GtfsDirectory(File("rawGtfs").toPath()))

        //packageData()
        val version = SimpleDateFormat("YYYYMMdd").format(Date()) + (revision ?: "")

        val packager = OCTranspoPackager(OcTranspoGtfsDirectory(tempDir))
        val dataPackage = packager.packageData(version)

        // Write data package to a json file
        val tempFile = Files.createTempFile("package-data", ".json")
        FileWriter(tempFile.toFile()).use {
            it.write(Json.stringify(DataPackage.serializer(), dataPackage))
        }

        // Dump our prepackaged database into a file
        val dumpFile = pgDumpPackage()

        // Zip all files required for the package
        zipFiles(
            "$version.zip",
            tempFile.toString() to "$version.json",
            dumpFile.toString() to "$version.pg",
            gtfsZip.absolutePath to "GTFS.zip"
        )
        println("Packaged data to $version.zip")
    }

    private fun configurePackagingDatabase() {
        val flyway = Flyway
            .configure()
            .dataSource("jdbc:postgresql://${Configuration.SQL_HOST}:${Configuration.SQL_PORT}/packaging", Configuration.SQL_USER, Configuration.SQL_PASSWORD)
            .load()

        flyway.clean()
        flyway.migrate()
        println("Configured packaging database")
    }

    private fun unzipGtfs() {
        // Unzip the given zip file
        ZipFile(gtfsZip).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    File(tempDir.toFile(), entry.name).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    /**
     * Dumps the packaged postgres database using pg_dump and returns a path to the dump
     * @return The dump file
     */
    private fun pgDumpPackage(): Path {
        val dumpFile = Files.createTempFile(null, ".pg")
        val properties = Properties().apply { load(File("config.properties").inputStream()) }
        val processBuilder = ProcessBuilder(
            properties["PG_DUMP"] as String,
            "--host", Configuration.SQL_HOST,
            "--port", Configuration.SQL_PORT,
            "--username", Configuration.SQL_USER,
            "--no-password",
            "--format=custom", "--clean",
            "--no-acl",
            "--file", dumpFile.toString(),
            "--exclude-table=live_updates", "--exclude-table=live_updates_stops",
            "--exclude-table=live_updates_routes", "--exclude-table=data_versions",
            "--exclude-table=flyway_schema_history", "--exclude-table=metadata",
            "--verbose", "packaging"
        )

        val env = processBuilder.environment()
        env["PGPASSWORD"] = Configuration.SQL_PASSWORD

        val process = processBuilder.start()
        BufferedReader(InputStreamReader(process.errorStream)).use {reader ->
            reader.lineSequence().forEach(System.err::println)
        }
        process.waitFor()
        println(process.exitValue())

        return dumpFile
    }
}