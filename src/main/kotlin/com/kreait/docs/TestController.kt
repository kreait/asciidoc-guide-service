package com.kreait.docs

import org.asciidoctor.Asciidoctor
import org.asciidoctor.Attributes
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.jsoup.Jsoup
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.social.github.api.impl.GitHubTemplate
import org.springframework.util.FileSystemUtils
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.StringWriter
import java.util.zip.ZipFile


@RestController
class TestController {


    val asciiDoctor = Asciidoctor.Factory.create()

    @GetMapping("/test")
    fun test(): String {

        val gitHubTemplate = GitHubTemplate()

        val byteArrayHttpMessageConverter = ByteArrayHttpMessageConverter()
        byteArrayHttpMessageConverter.supportedMediaTypes = listOf(MediaType.ALL)
        gitHubTemplate.restTemplate.messageConverters.add(byteArrayHttpMessageConverter)
        val download = gitHubTemplate.restOperations().getForObject("https://api.github.com/repos/kreait/slack-spring-boot-starter/zipball", ByteArray::class.java)

        try {

            val zipball = File.createTempFile("prefix", ".zip")
            zipball.deleteOnExit()
            val zipOut = FileOutputStream(zipball)
            zipOut.write(download)
            zipOut.close()

            var unzippedRoot: File? = null

            val zipFile = ZipFile(zipball)
            zipFile.stream()
                    .forEach {
                        if (it.isDirectory) {
                            val dir = File(zipball.parent + File.separator + it.name)
                            dir.mkdir()
                            if (unzippedRoot == null) {
                                unzippedRoot = dir // first directory is the root
                            }
                        } else {
                            StreamUtils.copy(zipFile.getInputStream(it),
                                    FileOutputStream(zipball.parent + File.separator + it.name))
                        }
                    }

            val attributes = Attributes()
            attributes.setAllowUriRead(true)
            attributes.setSkipFrontMatter(true)
            val readmeAdocFile = File(unzippedRoot?.absolutePath + File.separator + "README.adoc")
            val options = OptionsBuilder.options()
                    .safe(SafeMode.SAFE)
                    .baseDir(unzippedRoot)
                    .headerFooter(true)
                    .attributes(attributes)
            val writer = StringWriter()
            asciiDoctor.convert(FileReader(readmeAdocFile), writer, options)

            val doc = Jsoup.parse(writer.toString())


            // Delete the zipball and the unpacked content
            FileSystemUtils.deleteRecursively(zipball)
            FileSystemUtils.deleteRecursively(unzippedRoot)

            return doc.select("#content").html()
        } catch (e: Exception) {
            //lol
        }

        return "Hello World"
    }
}
