@file:JvmName("Main")

package ca.llamabagel.transpo.tools

import ca.llamabagel.transpo.tools.pack.PackageCommand
import ca.llamabagel.transpo.tools.shapes.ShapesCommand
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

const val SCHEMA_VERSION = 1

fun main(args: Array<String>) = Program()
    .subcommands(PackageCommand(), ConfigureCommand(), InfoCommand(), ShapesCommand())
    .main(args)

class Program : CliktCommand() {
    override fun run() {}
}