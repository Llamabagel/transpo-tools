@file:JvmName("Main")

package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.tools.configure.ConfigureCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import ca.llamabagel.transpo.tools.pack.PackageCommand

const val SCHEMA_VERSION = 1

fun main(args: Array<String>) {
    Program().subcommands(PackageCommand(), ConfigureCommand()).main(args)
}

class Program : CliktCommand() {
    override fun run() {
    }
}