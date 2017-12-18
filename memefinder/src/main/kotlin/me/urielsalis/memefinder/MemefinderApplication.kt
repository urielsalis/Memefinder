package me.urielsalis.memefinder

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MemefinderApplication

fun main(args: Array<String>) {
    SpringApplication.run(MemefinderApplication::class.java, *args)
}
