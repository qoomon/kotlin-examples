@file:Suppress("unused")

package me.qoomon.examples

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import me.qoomon.enhancements.kotlinlogging.info
import me.qoomon.enhancements.kotlinlogging.withLoggingContext

private val log = KotlinLogging.logger {}

class KotlinLoggingTestClass(private val log: KLogger = me.qoomon.examples.log) {

    fun doIt() {
        log.info { "log.info" }
    }
}

fun main() {
    log.info { "moin moin" }

    log.info({ "moin moin - with LoggingContext" }) {
        it["accountId"] = "123456789"
        it["basketId"] = "123456789"
    }

    withLoggingContext({
        it["accountId"] = "123456789"
        it["basketId"] = "123456789"
    }) {
        log.info { "moin moin - withLoggingContext()" }
    }
}
