package com.kreait.docs.controller

import com.kreait.docs.common.FileUtils
import com.kreait.docs.data.IndexItem
import com.kreait.docs.service.GithubService
import org.asciidoctor.Asciidoctor
import org.asciidoctor.jruby.GlobDirectoryWalker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController @Autowired constructor(val githubService: GithubService) {
    private val asciiDoctor: Asciidoctor = Asciidoctor.Factory.create()

    @GetMapping("/index/{org}/{repository}")
    fun index(@PathVariable("org") org: String, @PathVariable("repository") repo: String): List<IndexItem> {
        val zipball = FileUtils.createZipFile(githubService.downloadRepository(org, repo))
        val unzipped = FileUtils.unzipFile(zipball)
        //find all adoc files on root path
        val walker = GlobDirectoryWalker("${unzipped?.path}/*.adoc")

        val indexItems = mutableListOf<IndexItem>()
        walker.forEach {

            val document = asciiDoctor.loadFile(it, mapOf())
            indexItems.add(IndexItem(
                    it.name,
                    document.getAttribute("title").toString(),
                    document.content.toString()
            ))
        }
        return indexItems
    }
}