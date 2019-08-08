package com.kreait.docs.common

import com.kreait.docs.data.Guide
import org.asciidoctor.Asciidoctor
import org.asciidoctor.Options
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.asciidoctor.jruby.AsciiDocDirectoryWalker
import org.springframework.stereotype.Component
import java.io.File
import java.lang.IllegalArgumentException

@Component
class AsciiDocUtils {

    private var asciiDoctor = Asciidoctor.Factory.create()
    private val guideList = mutableListOf<Guide>()

    fun walkAndSort(dir: String, zipFile: File?, unzippedRoot: File?): MutableList<Guide> {

        val walker = AsciiDocDirectoryWalker(dir)
        walker.forEach { file ->
            val options = OptionsBuilder.options().baseDir(unzippedRoot)
            guideList.add(Guide(
                    id = file.nameWithoutExtension,
                    title = "${asciiDoctor.loadFile(file, options.asMap()).getAttribute("title")}",
                    order = asciiDoctor.loadFile(file, options.asMap()).getAttribute("order").toString().toInt(),
                    excerpt = "${asciiDoctor.loadFile(file, options.asMap().plus("doctype" to "article")).getAttribute("excerpt")}"
            ))
        }
        guideList.sortBy { it.order }
        FileUtils.cleanUp(zipFile, unzippedRoot)
        return guideList
    }

    fun assembleGuide(dir: String, id: String): Guide {

        val walker = AsciiDocDirectoryWalker(dir)
        var file: File? = null

        var index = 0
        var description = ""
        var title = ""

        walker.forEach { files ->
            if (files.isDirectory) {
                files?.listFiles()?.forEach {
                    if (it.nameWithoutExtension == id) {
                        file = it
                    }
                }
            } else if (files.nameWithoutExtension == id) {
                file = files
            }

            if (file == null) throw IllegalArgumentException("No guide with id $id found")

            file?.forEachLine {
                if (index == 0) {
                    title = it
                } else {
                    description += "$it \n"
                }
                index++
            }
        }

        val options = OptionsBuilder.options()
                .safe(SafeMode.UNSAFE)
                .docType("manpage")
                .baseDir(file?.parentFile)

        return Guide(id = file!!.nameWithoutExtension,
                title = title.replace("= ", ""),
                description = loadFile(file, options))
    }

    private fun loadFile(file: File?, options: OptionsBuilder): String {
        return asciiDoctor.loadFile(file, options.asMap().plus("doctype" to "article")).content.toString()
    }
}