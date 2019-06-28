package com.kreait.docs

import com.kreait.docs.common.FileUtils
import com.kreait.docs.data.Guide
import com.kreait.docs.data.GuideService
import org.asciidoctor.Asciidoctor
import org.asciidoctor.Attributes
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.asciidoctor.ast.Document
import org.asciidoctor.jruby.AsciiDocDirectoryWalker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
class GuideController @Autowired constructor(val guideService: GuideService) {
    private val asciiDoctor = Asciidoctor.Factory.create()
    private val EXCERPT_LENGTH = 200
    @GetMapping("/guides/{org}/{repository}")
    @Cacheable("guides")
    fun guides(@PathVariable("org") org: String, @PathVariable("repository") repository: String): MutableList<Guide> {
        val zipFile = downloadZip(org, repository)
        val unzippedRoot: File? = FileUtils.unzipFile(zipFile)
        val walker = AsciiDocDirectoryWalker(unzippedRoot?.absolutePath + File.separator + "guides")
        val guideList = mutableListOf<Guide>()

        walker.forEach { file ->
            var excerpt = ""
            var counter = 0
            var length = 0
            var title = ""
            file.bufferedReader().forEachLine {
                if (it.startsWith("image"))
                    excerpt += it
                else {
                    if (length <= EXCERPT_LENGTH) {
                        if (counter != 0) {
                            val text = it.take(EXCERPT_LENGTH - length) + "\n"
                            length += text.length
                            excerpt += text
                        } else {
                            title = "$it"
                        }
                        counter++

                    } else {
                        return@forEachLine
                    }
                }
            }

            val options = OptionsBuilder.options()
                    .baseDir(unzippedRoot)

            println(file.nameWithoutExtension)

            guideList.add(Guide(id = file.nameWithoutExtension,
                    title = title.replace("= ", ""),
                    excerpt = "${asciiDoctor.load(excerpt, options.asMap().plus("doctype" to "article")).content}"))
        }

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
        val file = walker.first {
            it.nameWithoutExtension == id
        }
        var index = 0
        var description = ""
        var title = ""
        file.forEachLine {
            if (index == 0) {
                title = it
            } else {
                description += "$it \n"
                println(description)
            }
            index++
        }
        val options = OptionsBuilder.options()
                .baseDir(unzippedRoot)
        return Guide(id = file.nameWithoutExtension,
                title = title.replace("= ", ""),
                excerpt = "${asciiDoctor.load(description, options.asMap().plus("doctype" to "article")).content}")
    }

    private fun loadFile(file: File, unzippedRoot: File): Document? {
        val attributes = Attributes()
        attributes.setAllowUriRead(true)
        attributes.setSkipFrontMatter(true)
        val options = OptionsBuilder.options()
                .safe(SafeMode.SAFE)
                .baseDir(unzippedRoot)
                .headerFooter(true)
                .attributes(attributes)
        return asciiDoctor.loadFile(file, options.asMap())
    }


    private fun downloadZip(org: String, repository: String): File? {
        try {
            return FileUtils.createZipFile(guideService.downloadGuides(org, repository))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
