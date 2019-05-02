package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand

class InfoCommand : CliktCommand(name = "info", help = "Displays info about the program (version, configuration)") {
    override fun run() {
        println("SQL_HOST: ${Configuration.SQL_HOST}")
        println("SQL_DATABASE: ${Configuration.SQL_DATABASE}")
        println("SQL_USER: ${Configuration.SQL_USER}")
        println("SQL_PASSWORD: ${Configuration.SQL_PASSWORD.map { "*" }.joinToString(separator = "")}")
        println("SQL_PORT: ${Configuration.SQL_PORT}")
        println("\nDATA_PACKAGE_DIRECTORY: ${Configuration.DATA_PACKAGE_DIRECTORY}")
    }
}