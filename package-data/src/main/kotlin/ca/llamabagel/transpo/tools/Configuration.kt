package ca.llamabagel.transpo.tools

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

object Configuration {
    val SQL_USER: String
    val SQL_HOST: String
    val SQL_PORT: String
    val SQL_PASSWORD: String

    init {
        if (!Files.exists(Paths.get("config.properties"))) {
            throw FileNotFoundException("config.properties file not found.")
        }

        val properties = Properties().apply {
            load(FileInputStream("config.properties"))
        }

        SQL_USER = properties.getProperty("SQL_USER") ?: throw IllegalStateException("SQL_USER not set in config.properties")
        SQL_HOST = properties.getProperty("SQL_HOST") ?: throw IllegalStateException("SQL_HOST not set in config.properties")
        SQL_PORT = properties.getProperty("SQL_PORT") ?: throw IllegalStateException("SQL_PORT not set in config.properties")
        SQL_PASSWORD = properties.getProperty("SQL_PASSWORD") ?: throw IllegalStateException("SQL_PASSWORD not set in config.properties")
    }

    @JvmStatic
    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection("jdbc:postgresql//$SQL_HOST:$SQL_PORT/transit", SQL_USER, SQL_PASSWORD)
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}