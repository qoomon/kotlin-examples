package me.qoomon.examples

import mu.KLogger
import mu.KotlinLogging

internal val LOG: KLogger = KotlinLogging.logger {}

class Service(private val log: KLogger = LOG) {

    fun doIt() {
        log.info { "it's done :D" }
    }
}
