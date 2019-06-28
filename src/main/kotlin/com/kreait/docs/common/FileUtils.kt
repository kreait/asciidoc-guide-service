package com.kreait.docs.common

import org.springframework.util.FileSystemUtils
import org.springframework.util.StreamUtils
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

class FileUtils() {
    companion object {
        fun createZipFile(content: ByteArray): File? {
            val zipball = File.createTempFile("prefix", ".zip")
            zipball.deleteOnExit()
            val zipOut = FileOutputStream(zipball)
            zipOut.write(content)
            zipOut.close()
            return zipball
        }

        fun unzipFile(zipball: File?): File? {
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
        }

        fun cleanUp(zipFile: File?, unzippedRoot: File?) {
            FileSystemUtils.deleteRecursively(zipFile)
            FileSystemUtils.deleteRecursively(unzippedRoot)
        }
    }
}