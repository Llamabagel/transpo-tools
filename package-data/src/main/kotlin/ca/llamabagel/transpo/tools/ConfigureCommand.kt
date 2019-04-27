package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.tools.Configuration
import com.github.ajalt.clikt.core.CliktCommand
import org.flywaydb.core.Flyway
import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
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

    private fun validateConfig(config: Properties): Boolean {
        fun validateProperty(property: String): Boolean {
            return if (!config.containsKey(property)) {
                println("$property not defined in config.properties")
                false
            } else true
        }

        return validateProperty("SQL_HOST") &&
                validateProperty("SQL_PORT") &&
                validateProperty("SQL_USER") &&
                validateProperty("SQL_PASSWORD")
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