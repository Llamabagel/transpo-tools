package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand

class InfoCommand : CliktCommand(name = "info", help = "Displays info about the program (version, configuration)") {
    override fun run() {
        println("SQL_HOST: localhost")
        println("SQL_DATABASE: transit")
        println("SQL_USER: transpo")
        println("SQL_PASSWORD: ${"packaging".map { "*" }.joinToString(separator = "")}")
        println("SQL_PORT: 5432")
        println("\nDATA_PACKAGE_DIRECTORY: /tmp")
    }
}