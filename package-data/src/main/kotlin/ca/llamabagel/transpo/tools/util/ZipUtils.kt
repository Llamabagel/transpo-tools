package ca.llamabagel.transpo.tools.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Create a zip file of the given list of paths to files
 * @param zipPath The path for the zip file in which all files will be zipped into
 * @param files The files to be zipped
 */
fun zipFiles(zipPath: String, vararg files: String) {
    zipFiles(zipPath, *files.map { it to it }.toTypedArray())
}

/**
 * Create a zip file with the specified destination where each file is zipped to a specific path inside of the zip.
 * @param zipPath The path for the zip file in which all files will be zipped into
 * @param files The list of files to be zipped mapped to their destination paths inside the zip
 */
fun zipFiles(zipPath: String, vararg files: Pair<String, String>) {
    val zipOutputStream = ZipOutputStream(FileOutputStream(zipPath))

    for (file in files) {
        val fileToZip = File(file.first)
        zipOutputStream.putNextEntry(ZipEntry(file.second))

        val inputStream = FileInputStream(fileToZip)
        val bytes = ByteArray(1024)
        var length: Int
        while (inputStream.read(bytes).also { length = it } >= 0) {
            zipOutputStream.write(bytes, 0, length)
        }
        inputStream.close()

        // Files.copy(fileToZip.toPath(), zipOutputStream)
    }
    zipOutputStream.close()
}

/**
 * Unzip a file ([zipFile]) to a [destination]
 */
fun unzipFile(zipFile: File, destination: File) {
    ZipFile(zipFile).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                File(destination, entry.name).outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}