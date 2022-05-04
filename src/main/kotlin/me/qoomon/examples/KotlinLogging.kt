package me.qoomon.examples

import mu.KLogger
import mu.KotlinLogging
import mu.withLoggingContext

private val log: KLogger = KotlinLogging.logger {}

class KotlinLoggingTestClass(private val log: KLogger = me.qoomon.examples.log) {

    fun doIt() {
        log.info { "log.info" }
    }
}

class LoggingFields {
    val fields: MutableMap<String, String?> = mutableMapOf()
    fun field(pair: Pair<String, String?>) = fields.put(pair.first, pair.second)
}

private fun KLogger.info(msg: () -> String, fields: LoggingFields.() -> Unit) {
    if (isInfoEnabled) {
        val context = LoggingFields().apply { fields() }
        withLoggingContext(context.fields) {
            return info(msg)
        }
    }
}

fun main() {

    log.info { "log.info" }

    withLoggingContext("accountId" to "123456789") {
        log.info { "withLoggingContext > log.info" }
    }

    log.info({ "log.info with fields" }, {
        field("accountId" to "123456789")
        field("basketId" to "123456789")
    })
}
