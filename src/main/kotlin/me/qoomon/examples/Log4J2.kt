package me.qoomon.examples

import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger

private val LOG = logger {}

class Log4J2TestClass(private val log: KotlinLogger = LOG) {

    fun doIt() {
        log.info { "it's done :D" }
    }

    companion object : Logging
}

fun main() {
    Log4J2TestClass().doIt()
}

fun logger(_context: () -> Unit) = logger(
    with(_context::class.java.name) {
        when {
            contains("Kt$") -> substringBefore("Kt$")
            contains("$") -> substringBefore("$")
            else -> this
        }
    }
)
