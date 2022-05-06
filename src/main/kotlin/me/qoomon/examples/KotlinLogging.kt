package me.qoomon.examples

import mu.KLogger
import mu.KotlinLogging
import mu.withLoggingContext

private val log = KotlinLogging.logger {}

class KotlinLoggingTestClass(private val log: KLogger = me.qoomon.examples.log) {

    fun doIt() {
        log.info { "log.info" }
    }
}

private fun KLogger.info(msg: () -> String, fields: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        val context = mutableMapOf<String, String?>().apply { fields(this) }
        withLoggingContext(context) {
            return info(msg)
        }
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
