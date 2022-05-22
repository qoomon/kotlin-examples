@file:Suppress("unused")

package me.qoomon.examples

import me.qoomon.enhancements.kotlinlogging.info
import mu.KLogger
import mu.KotlinLogging
import mu.withLoggingContext

private val log = KotlinLogging.logger {}

class KotlinLoggingTestClass(private val log: KLogger = me.qoomon.examples.log) {

    fun doIt() {
        log.info { "log.info" }
    }
}

fun main() {

    log.info { "log.info" }

    log.info({ "log.info with fields" }, {
        it["accountId"] = "123456789"
        it["basketId"] = "123456789"
    })

    withLoggingContext("accountId" to "123456789") {
        log.info { "withLoggingContext > log.info" }
    }
}
