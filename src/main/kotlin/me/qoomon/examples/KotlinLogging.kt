package me.qoomon.examples

import mu.KLogger
import mu.KotlinLogging

private val LOG: KLogger = KotlinLogging.logger {}

class KotlinLoggingTestClass(private val log: KLogger = LOG) {

    fun doIt() {
        log.info { "it's done :D" }
    }
}

fun main() {
    KotlinLoggingTestClass().doIt()
}
