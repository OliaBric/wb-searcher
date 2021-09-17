package com.oliabric.wbsearcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class WbsearcherApplication

fun main(args: Array<String>) {
    runApplication<WbsearcherApplication>(*args)
}

//@RestController
//class HelloController {
//    @GetMapping("/")
//    fun index() = "Hello"
//}

