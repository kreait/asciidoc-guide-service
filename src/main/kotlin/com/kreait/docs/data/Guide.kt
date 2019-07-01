package com.kreait.docs.data

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.social.github.api.impl.GitHubTemplate
import org.springframework.stereotype.Service

@Service
class GuideService {


    @Cacheable("cache.guides")
    fun getGuides(): List<Guide> {
        return emptyList()
    }

    fun downloadGuides(org: String, repository: String): ByteArray {
        val gitHubTemplate = GitHubTemplate()
        val byteArrayHttpMessageConverter = ByteArrayHttpMessageConverter()
        byteArrayHttpMessageConverter.supportedMediaTypes = listOf(MediaType.ALL)
        gitHubTemplate.restTemplate.messageConverters.add(byteArrayHttpMessageConverter)
        return gitHubTemplate.restOperations().getForObject("https://api.github.com/repos/$org/$repository/zipball", ByteArray::class.java)!!
    }
}

data class Guide(val id: String, val title: String, val description: String, val parents: List<String> = listOf())