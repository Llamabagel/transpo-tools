package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.flywaydb.core.Flyway
import kotlin.system.exitProcess

class ConfigureCommand :
    CliktCommand(
        name = "configure",
        help = "Creates the database schema for the transit database",
        epilog = "Uses the data in the config.properties file to connect to the transit database and creates the schema required to store the transit data."
    ) {

    private val configFile by option(
        "-c",
        "--config",
        help = "A config file that specify certain values that will be used by the tool."
    )
        .file(folderOkay = false)
    private val config by lazy { getConfig(configFile) }

    override fun run() {
        val flyway = getDbConnection()
        if (flyway == null) {
            println("Could not establish a connection to SQL server")
            exitProcess(1)
        }

        flyway.clean()
        flyway.migrate()
    }

    private fun getDbConnection(): Flyway? {
        return try {
            val sql = config.sql
            Flyway
                .configure()
                .dataSource("jdbc:postgresql://${sql.host}:${sql.port}/${sql.database}", sql.user, sql.password)
                .load()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}