package com.yhproject.mywiki

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyWikiApplication

fun main(args: Array<String>) {
    runApplication<MyWikiApplication>(*args)
}
