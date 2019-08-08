package com.kreait.docs.common

import com.kreait.docs.service.GithubService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.FileSystemUtils
import org.springframework.util.StreamUtils
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

@Component
class FileUtils @Autowired constructor(private val ghService: GithubService) {

    companion object {

        fun createZipFile(content: ByteArray): File? {
            val zipball = File.createTempFile("prefix", ".zip")
            zipball.deleteOnExit()
            val zipOut = FileOutputStream(zipball)
            zipOut.write(content)
            zipOut.close()
            return zipball
        }

        fun cleanUp(zipFile: File?, unzippedRoot: File?) {
            FileSystemUtils.deleteRecursively(zipFile)
            FileSystemUtils.deleteRecursively(unzippedRoot)
        }
    }

    fun downloadZip(org: String, repository: String): File? {
        try {
            return createZipFile(ghService.downloadRepository(org, repository))
        } catch (e: Exception) {
            println("Could not find repository.")
        }
        return null
    }

    fun unzipFile(zipball: File?): File? {
        try {
            val zipFile = ZipFile(zipball)
            var unzipped: File? = null
            zipFile.stream()
                    .forEach {
                        if (it.isDirectory) {
                            val dir = File(zipball?.parent + File.separator + it.name)
                            dir.mkdir()
                            if (unzipped == null) {
                                unzipped = dir // first directory is the root
                            }
                        } else {
                            StreamUtils.copy(zipFile.getInputStream(it),
                                    FileOutputStream(zipball?.parent + File.separator + it.name))
                        }
                    }
            return unzipped
        } catch (e: Exception) {
            println("Unable to unzip file. Attempted to extract a non-existing file.")
            return null
        }
    }
}