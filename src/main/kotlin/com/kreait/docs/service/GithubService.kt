package com.kreait.docs.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.social.github.api.impl.GitHubTemplate
import org.springframework.stereotype.Service

@Service
class GithubService {

    @Cacheable("cache.guides")
    fun downloadRepository(org: String, repository: String): ByteArray {
        val gitHubTemplate = GitHubTemplate()
        val byteArrayHttpMessageConverter = ByteArrayHttpMessageConverter()
        byteArrayHttpMessageConverter.supportedMediaTypes = listOf(MediaType.ALL)
        gitHubTemplate.restTemplate.messageConverters.add(byteArrayHttpMessageConverter)
        return gitHubTemplate.restOperations().getForObject("https://api.github.com/repos/$org/$repository/zipball", ByteArray::class.java)!!
    }
}