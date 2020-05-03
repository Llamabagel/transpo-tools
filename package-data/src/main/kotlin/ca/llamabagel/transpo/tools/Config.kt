package ca.llamabagel.transpo.tools

import java.io.File
import java.util.Properties

fun getConfig(file: File? = null): Config {
    val properties = Properties()
    if (file?.exists() == true) {
        properties.load(file.inputStream())
    }

    val sqlConfig = SqlConfig(
        properties.getProperty("SQL_PORT", "5432"),
        properties.getProperty("SQL_HOST", "localhost"),
        properties.getProperty("SQL_USER", "postgres"),
        properties.getProperty("SQL_PASSWORD", "postgres"),
        properties.getProperty("SQL_DATABASE", "postgres"),
        properties.getProperty("PG_DUMP", "pg_dump"),
        properties.getProperty("PG_RESTORE", "pg_restore")
    )

    val transpoConfig = TranspoConfig(
        properties.getProperty("TRANSPO_SCHEMA_VERSION", "1").toIntOrNull() ?: 1,
        properties.getProperty("DATA_PACKAGE_DIRECTORY", "/tmp")
    )

    val osrmConfig =
        OsrmConfig(properties.getProperty("OSRM_HOST", "localhost"), properties.getProperty("OSRM_PORT", "5000"))

    return Config(sqlConfig, transpoConfig, osrmConfig)
}

data class Config(
    val sql: SqlConfig,
    val transpo: TranspoConfig,
    val osrm: OsrmConfig
)

data class SqlConfig(
    val port: String,
    val host: String,
    val user: String,
    val password: String,
    val database: String,
    val pgDumpLocation: String,
    val pgRestoreLocation: String
)

data class TranspoConfig(
    val schemaVersion: Int,
    val dataDirectory: String
)

data class OsrmConfig(
    val host: String,
    val port: String
)