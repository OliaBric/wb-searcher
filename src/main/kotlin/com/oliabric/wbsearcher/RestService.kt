package com.oliabric.wbsearcher

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service


@Service
class RestService(restTemplateBuilder: RestTemplateBuilder) {
    private var restTemplate = restTemplateBuilder.build()

    fun getPostsPlainJSON(url: String): String? {
        return restTemplate.getForObject(url, String::class.java)
    }
}