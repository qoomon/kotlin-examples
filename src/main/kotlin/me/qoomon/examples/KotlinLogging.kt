@file:Suppress("unused")

package me.qoomon.examples

import mu.KLogger
import mu.KotlinLogging
import mu.withLoggingContext
import org.slf4j.Marker

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

// --- KLogger Extension Functions ------------------------------------------------------------------------------------

/**
 * Lazy add a log message if isTraceEnabled is true
 */
fun KLogger.trace(msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { trace(msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
fun KLogger.debug(msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(fields.invoke()) { debug(msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled is true
 */
fun KLogger.info(msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(fields.invoke()) { info(msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled is true
 */
fun KLogger.warn(msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(fields.invoke()) { warn(msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled is true
 */
fun KLogger.error(msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(fields.invoke()) { error(msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled is true
 */
fun KLogger.trace(t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { trace(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled is true
 */
fun KLogger.debug(t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(fields.invoke()) { debug(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled is true
 */
fun KLogger.info(t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(fields.invoke()) { info(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled is true
 */
fun KLogger.warn(t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(fields.invoke()) { warn(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled is true
 */
fun KLogger.error(t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(fields.invoke()) { error(t, msg) }
    }
}

/**
 * Lazy add a log message if isTraceEnabled is true
 */
fun KLogger.trace(marker: Marker?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { trace(marker, msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
fun KLogger.debug(marker: Marker?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(fields.invoke()) { debug(marker, msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled is true
 */
fun KLogger.info(marker: Marker?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(fields.invoke()) { info(marker, msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled is true
 */
fun KLogger.warn(marker: Marker?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(fields.invoke()) { warn(marker, msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled is true
 */
fun KLogger.error(marker: Marker?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(fields.invoke()) { error(marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled is true
 */
fun KLogger.trace(marker: Marker?, t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { trace(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled is true
 */
fun KLogger.debug(marker: Marker?, t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(fields.invoke()) { debug(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled is true
 */
fun KLogger.info(marker: Marker?, t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(fields.invoke()) { info(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled is true
 */
fun KLogger.warn(marker: Marker?, t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(fields.invoke()) { warn(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled is true
 */
fun KLogger.error(marker: Marker?, t: Throwable?, msg: () -> Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(fields.invoke()) { error(marker, t, msg) }
    }
}

/**
 * Add a log message with all the supplied parameters along with method name
 */
fun KLogger.entry(vararg argArray: Any?, fields: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { entry(argArray) }
    }
}

/**
 * Add log message indicating exit of a method
 */
fun KLogger.exit(fields: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { exit() }
    }
}

/**
 * Add a log message with the return value of a method
 */
fun <T> KLogger.exit(result: T, fields: (MutableMap<String, String?>) -> Unit): T where T : Any? {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { exit(result) }
    }
    return result
}

/**
 * Add a log message indicating an exception will be thrown along with the stack trace.
 */
fun <T> KLogger.throwing(throwable: T, fields: (MutableMap<String, String?>) -> Unit) where T : Throwable {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { throwing(throwable) }
    }
}

/**
 * Add a log message indicating an exception is caught along with the stack trace.
 */
fun <T> KLogger.catching(throwable: T, fields: (MutableMap<String, String?>) -> Unit) where T : Throwable {
    if (isTraceEnabled) {
        withLoggingContext(fields.invoke()) { catching(throwable) }
    }
}

private fun ((MutableMap<String, String?>) -> Unit).invoke(): MutableMap<String, String?> {
    val context = mutableMapOf<String, String?>()
    this(context)
    return context
}
