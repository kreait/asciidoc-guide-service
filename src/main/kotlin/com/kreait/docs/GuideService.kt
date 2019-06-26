package com.kreait.docs

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


    private fun map() {

    }

    private fun download(): ByteArray {
        val gitHubTemplate = GitHubTemplate()
        val byteArrayHttpMessageConverter = ByteArrayHttpMessageConverter()
        byteArrayHttpMessageConverter.supportedMediaTypes = listOf(MediaType.ALL)
        gitHubTemplate.restTemplate.messageConverters.add(byteArrayHttpMessageConverter)
        return gitHubTemplate.restOperations().getForObject("https://api.github.com/repos/kreait/slack-spring-boot-starter/zipball", ByteArray::class.java)!!

    }


}

data class Guide(val title: String, val excerpt: String, val content: String)
