package com.kreait.docs.controller

import com.kreait.docs.common.FileUtils
import com.kreait.docs.data.Guide
import com.kreait.docs.service.GithubService
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.asciidoctor.jruby.AsciiDocDirectoryWalker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
class GuideController @Autowired constructor(val guideService: GithubService) {
    private lateinit var asciiDoctor: Asciidoctor

    @GetMapping("/guides/{org}/{repository}")
    @Cacheable("guides")
    fun guides(@PathVariable("org") org: String, @PathVariable("repository") repository: String): MutableList<Guide> {
        val zipFile = downloadZip(org, repository)
        val unzippedRoot: File? = FileUtils.unzipFile(zipFile)
        val baseDir = unzippedRoot?.absolutePath + File.separator + "guides"
        val walker = AsciiDocDirectoryWalker(baseDir)
        asciiDoctor = Asciidoctor.Factory.create()
        val guideList = mutableListOf<Guide>()

        walker.forEach { file ->
            val options = OptionsBuilder.options()
                    .baseDir(unzippedRoot)
            guideList.add(Guide(id = file.nameWithoutExtension,
                    title = "${asciiDoctor.loadFile(file, options.asMap()).getAttribute("title")}",
                    order = asciiDoctor.loadFile(file, options.asMap()).getAttribute("order").toString().toInt(),
                    excerpt = "${asciiDoctor.loadFile(file, options.asMap().plus("doctype" to "article")).getAttribute("excerpt")}"))
        }
        guideList.sortBy { it.order }

        FileUtils.cleanUp(zipFile, unzippedRoot)
        return guideList
    }

    @GetMapping("/guides/{org}/{repository}/{id}")
    //@Cacheable("guide")
    fun getGuide(@PathVariable("org") org: String,
                 @PathVariable("repository") repository: String,
                 @PathVariable("id") id: String): Guide {
        val zipFile = downloadZip(org, repository)
        val unzippedRoot: File? = FileUtils.unzipFile(zipFile)
        val walker = AsciiDocDirectoryWalker(unzippedRoot?.absolutePath + File.separator + "guides")

        var file: File? = null
        walker.forEach { files ->
            if (files.isDirectory) {
                files.listFiles().forEach {
                    if (it.nameWithoutExtension == id)
                        file = it
                }
            } else if (files.nameWithoutExtension == id)
                file = files
        }

        if (file == null) {
            throw IllegalArgumentException("no guide with id $id found")
        }
        var index = 0
        var description = ""
        var title = ""
        file?.forEachLine {
            if (index == 0) {
                title = it
            } else {
                description += "$it \n"
            }
            index++
        }
        val options = OptionsBuilder.options()
                .safe(SafeMode.UNSAFE)
                .docType("manpage")
                .baseDir(file?.parentFile)
        return Guide(id = file!!.nameWithoutExtension,
                title = title.replace("= ", ""),
                description = asciiDoctor.loadFile(file, options.asMap().plus("doctype" to "article")).content.toString())
    }

    private fun downloadZip(org: String, repository: String): File? {
        try {
            return FileUtils.createZipFile(guideService.downloadRepository(org, repository))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
