@file:JvmName("Main")

package ca.llamabagel.transpo.tools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import pack.PackageCommand

fun main(args: Array<String>) {
    Program().subcommands(PackageCommand()).main(args)
}

class Program : CliktCommand() {
    override fun run() {
    }
}