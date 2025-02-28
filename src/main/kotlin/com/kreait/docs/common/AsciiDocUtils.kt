package com.kreait.docs.common

import com.kreait.docs.data.Guide
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.asciidoctor.SafeMode
import org.asciidoctor.jruby.AsciiDocDirectoryWalker
import org.springframework.stereotype.Component
import java.io.File

@Component
class AsciiDocUtils {

    private var asciiDoctor = Asciidoctor.Factory.create()

    fun walkAndSort(dir: String, zipFile: File?, unzippedRoot: File?): MutableList<Guide> {
        val guideList = mutableListOf<Guide>()
        val walker = AsciiDocDirectoryWalker(dir)


        walker.forEach { file ->
            val options = OptionsBuilder.options().baseDir(unzippedRoot)
            val optionsDoctype = options.asMap().plus("doctype" to "article")
            val loadedFile = asciiDoctor.loadFile(file, options.asMap())

            guideList.add(Guide(
                    id = file.nameWithoutExtension,
                    title = "${loadedFile.getAttribute("title")}",
                    order = loadedFile.getAttribute("order").toString().toInt(),
                    excerpt = "${asciiDoctor.loadFile(file, optionsDoctype).getAttribute("excerpt")}"
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