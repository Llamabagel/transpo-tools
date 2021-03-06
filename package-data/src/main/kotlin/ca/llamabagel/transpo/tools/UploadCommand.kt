package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties
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
    private val configFile by option(
        "-c",
        "--config",
        help = "A config file that specify certain values that will be used by the tool."
    )
        .file(folderOkay = false)
    private val config by lazy { getConfig(configFile) }

    private val dbConnection: Connection by lazy {
        DriverManager.getConnection(
            "jdbc:postgresql://${config.sql.host}:${config.sql.port}/${config.sql.database}",
            config.sql.user,
            config.sql.password
        ) ?: throw IllegalStateException()
    }
    private val directoryPath by lazy { "${config.transpo.dataDirectory}/$SCHEMA_VERSION/$version" }

    override fun run() {
        val file = File("$version.zip")

        if (!file.exists()) {
            println("Could not find data package for version: $version")
            return
        }

        println("Unzipping package")
        unzipPackage(file)

        println("Restoring database")
        uploadData()
        println("Finished restoring database")

        writeMetadata()

        println("Updated data to version $version")
    }

    private fun unzipPackage(zipFile: File) {
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

    private fun writeMetadata() {
        println("Writing metadata...")
        val statement = dbConnection.createStatement()

        // Insert metadata
        statement.execute("INSERT INTO data_versions (version, schema_version) VALUES ($version, $SCHEMA_VERSION)")
        statement.execute("INSERT INTO metadata VALUES ('android', '$version', $SCHEMA_VERSION, now(), 1) ON CONFLICT (platform) DO UPDATE SET data_version = $version, schema_version = $SCHEMA_VERSION")
        println("Done.")
    }

    private fun uploadData(): Int {
        val properties = Properties().apply { load(File("config.properties").inputStream()) }
        val processBuilder = ProcessBuilder(
            properties["PG_RESTORE"] as String,
            "--host", config.sql.host,
            "--port", config.sql.port,
            "--username", config.sql.user,
            "--no-password",
            "--format=custom", "--clean",
            "--table=agencies", "--table=calendar_dates",
            "--table=calendars", "--table=routes",
            "--table=shapes", "--table=stop_times",
            "--table=stops", "--table=trips",
            "--dbname=${config.sql.database}",
            "--verbose",
            "$directoryPath/$version.pg"
        )

        val env = processBuilder.environment()
        env["PGPASSWORD"] = config.sql.password

        val process = processBuilder.start()
        BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
            reader.lineSequence().forEach(System.err::println)
        }
        process.waitFor()
        println(process.exitValue())

        return process.exitValue()
    }
}