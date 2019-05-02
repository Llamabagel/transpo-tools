package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand
import org.flywaydb.core.Flyway
import kotlin.system.exitProcess

class ConfigureCommand : CliktCommand(name = "configure") {
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
            Flyway.configure().dataSource("jdbc:postgresql://${Configuration.SQL_HOST}:${Configuration.SQL_PORT}/transit",
                Configuration.SQL_USER, Configuration.SQL_PASSWORD)
                .load()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}