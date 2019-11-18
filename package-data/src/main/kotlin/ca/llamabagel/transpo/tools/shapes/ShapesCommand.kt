package ca.llamabagel.transpo.tools.shapes

import ca.llamabagel.transpo.tools.util.unzipFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import java.nio.file.Files

class ShapesCommand : CliktCommand(
    name = "shapes",
    help = "Generate the shapes.txt file for a gtfs data zip"
) {
    private val gtfsZip by argument(help = "The GTFS zip file").file(exists = true, readable = true, folderOkay = false)
    private val tempDir = Files.createTempDirectory("shapes")

    override fun run() {
        unzipFile(gtfsZip, tempDir.toFile())

    }
}