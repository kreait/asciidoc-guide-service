package com.kreait.docs.controller

import com.kreait.docs.common.AsciiDocUtils
import com.kreait.docs.common.FileUtils
import com.kreait.docs.data.Guide
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
class GuideController @Autowired constructor(private val fileUtils: FileUtils,
                                             private val asciiDocUtils: AsciiDocUtils) {

    @GetMapping("/guides/{org}/{repository}")
    @Cacheable("guides")
    fun guides(@PathVariable("org") org: String, @PathVariable("repository") repository: String): MutableList<Guide> {

        val zipFile = fileUtils.downloadZip(org, repository)
        val unzippedRoot = fileUtils.unzipFile(zipFile)
        val baseDir = unzippedRoot?.absolutePath + File.separator + "guides"

        return asciiDocUtils.walkAndSort(baseDir, zipFile, unzippedRoot)
    }

    @GetMapping("/guides/{org}/{repository}/{id}")
    @Cacheable("guide")
    fun getGuide(@PathVariable("org") org: String,
                 @PathVariable("repository") repository: String,
                 @PathVariable("id") id: String): Guide {

        val zipFile = fileUtils.downloadZip(org, repository)
        val unzippedRoot: File? = fileUtils.unzipFile(zipFile)
        val baseDir = unzippedRoot?.absolutePath + File.separator + "guides"

        return asciiDocUtils.assembleGuide(baseDir, id)
    }
}
