package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand
import org.flywaydb.core.Flyway
import kotlin.system.exitProcess

class ConfigureCommand :
    CliktCommand(
        name = "configure",
        help = "Creates the database schema for the transit database",
        epilog = "Uses the data in the config.properties file to connect to the transit database and creates the schema required to store the transit data."
    ) {
    override fun run() {
        val flyway = getDbConnection()
        if (flyway == null) {
            println("Could not establish a connection to SQL server")
            exitProcess(1)
        }

        flyway.migrate()
    }

    private fun getDbConnection(): Flyway? {
        return try {
            Flyway.configure().dataSource(
                "jdbc:postgresql://${Configuration.SQL_HOST}:${Configuration.SQL_PORT}/transit",
                Configuration.SQL_USER, Configuration.SQL_PASSWORD
            )
                .load()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}