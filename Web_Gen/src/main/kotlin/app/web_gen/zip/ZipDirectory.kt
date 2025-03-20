package app.web_gen.zip

import app.web_gen.project.ProjectPathResolver
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Scanner
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path
import kotlin.io.path.pathString

@Service
class ZipDirectory(
    private val pathResolver: ProjectPathResolver
) {
    val exclude = mutableSetOf<String>()
    private val filePath =
        Path(pathResolver.getResourcesPath(), "excludeFoldersInZip.txt").pathString

    init {
        loadCommands()
    }

    private final fun loadCommands() {
        println("ZipDirectory Config Filepath: $filePath")
        val file = File(filePath)
        if (file.exists()) {
            val sc = Scanner(file)
            while (sc.hasNextLine()) {
                val line = sc.nextLine()
                exclude.add(line.trim())
            }
        } else {
            file.createNewFile()
        }

        println("exclude: $exclude")
    }

    fun zip(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
        zipFile(fileToZip, fileName, zipOut)
        zipOut.finish()
        zipOut.flush()
        IOUtils.closeQuietly(zipOut)
    }

    @Throws(IOException::class)
    private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
        if (fileToZip.isHidden) {
            return
        }

        if (fileToZip.isDirectory && exclude.contains(fileToZip.name))
            return

        if (fileToZip.isDirectory) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(ZipEntry(fileName))
                zipOut.closeEntry()
            } else {
                zipOut.putNextEntry(ZipEntry("$fileName/"))
                zipOut.closeEntry()
            }
            val children = fileToZip.listFiles()
            for (childFile in children ?: arrayOf()) {
                zipFile(childFile, fileName + "/" + childFile.name, zipOut)
            }
            return
        }
        val fis = FileInputStream(fileToZip)
        val zipEntry = ZipEntry(fileName)
        zipOut.putNextEntry(zipEntry)
        val bytes = ByteArray(1024)
        var length: Int
        while ((fis.read(bytes).also { length = it }) >= 0) {
            zipOut.write(bytes, 0, length)
        }
        fis.close()
    }
}