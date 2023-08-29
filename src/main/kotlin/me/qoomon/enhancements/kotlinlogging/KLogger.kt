@file:Suppress("complexity.TooManyFunctions")
package me.qoomon.enhancements.kotlinlogging

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.Marker

typealias LoggingContext = MutableMap<String, String?>

// --- KLogger Lazy Context Extensions ---------------------------------------------------------------------------------

/**
 * Lazy add a log message if isTraceEnabled() is true
 */
fun KLogger.trace(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isTraceEnabled()) {
        withLoggingContext(loggingContext) { trace(msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled() is true
 */
fun KLogger.debug(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isDebugEnabled()) {
        withLoggingContext(loggingContext) { debug(msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled() is true
 */
fun KLogger.info(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isInfoEnabled()) {
        withLoggingContext(loggingContext) { info(msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled() is true
 */
fun KLogger.warn(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isWarnEnabled()) {
        withLoggingContext(loggingContext) { warn(msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled() is true
 */
fun KLogger.error(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isErrorEnabled()) {
        withLoggingContext(loggingContext) { error(msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled() is true
 */
fun KLogger.trace(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isTraceEnabled()) {
        withLoggingContext(loggingContext) { trace(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled() is true
 */
fun KLogger.debug(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isDebugEnabled()) {
        withLoggingContext(loggingContext) { debug(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled() is true
 */
fun KLogger.info(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isInfoEnabled()) {
        withLoggingContext(loggingContext) { info(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled() is true
 */
fun KLogger.warn(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isWarnEnabled()) {
        withLoggingContext(loggingContext) { warn(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled() is true
 */
fun KLogger.error(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isErrorEnabled()) {
        withLoggingContext(loggingContext) { error(t, msg) }
    }
}

/**
 * Lazy add a log message if isTraceEnabled() is true
 */
fun KLogger.trace(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isTraceEnabled(marker)) {
        withLoggingContext(loggingContext) { trace(null as Throwable?, marker, msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled() is true
 */
fun KLogger.debug(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isDebugEnabled(marker)) {
        withLoggingContext(loggingContext) { debug(null as Throwable?, marker, msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled() is true
 */
fun KLogger.info(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isInfoEnabled(marker)) {
        withLoggingContext(loggingContext) { info(null as Throwable?, marker, msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled() is true
 */
fun KLogger.warn(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isWarnEnabled(marker)) {
        withLoggingContext(loggingContext) { warn(null as Throwable?, marker, msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled() is true
 */
fun KLogger.error(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isErrorEnabled(marker)) {
        withLoggingContext(loggingContext) { error(null as Throwable?, marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled() is true
 */
fun KLogger.trace(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isTraceEnabled(marker)) {
        withLoggingContext(loggingContext) { trace(t, marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled() is true
 */
fun KLogger.debug(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isDebugEnabled(marker)) {
        withLoggingContext(loggingContext) { debug(t, marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled() is true
 */
fun KLogger.info(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isInfoEnabled(marker)) {
        withLoggingContext(loggingContext) { info(t, marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled() is true
 */
fun KLogger.warn(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isWarnEnabled(marker)) {
        withLoggingContext(loggingContext) { warn(t, marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled() is true
 */
fun KLogger.error(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isErrorEnabled(marker)) {
        withLoggingContext(loggingContext) { error(t, marker, msg) }
    }
}

/**
 * Add a log message with all the supplied parameters along with method name
 */
fun KLogger.entry(
    vararg argArray: Any?,
    loggingContext: (LoggingContext) -> Unit,
) {
    if (isTraceEnabled()) {
        withLoggingContext(loggingContext) { entry(argArray) }
    }
}

/**
 * Add a log message indicating an exception will be thrown along with the stack trace.
 */
fun <T> KLogger.throwing(throwable: T, loggingContext: (LoggingContext) -> Unit) where T : Throwable {
    if (isTraceEnabled()) {
        withLoggingContext(loggingContext) { throwing(throwable) }
    }
}

/**
 * Add a log message indicating an exception is caught along with the stack trace.
 */
fun <T> KLogger.catching(throwable: T, loggingContext: (LoggingContext) -> Unit) where T : Throwable {
    if (isTraceEnabled()) {
        withLoggingContext(loggingContext) { catching(throwable) }
    }
}

inline fun <T> withLoggingContext(
    loggingContext: (LoggingContext) -> Unit,
    restorePrevious: Boolean = true,
    body: () -> T,
) = io.github.oshai.kotlinlogging.withLoggingContext(
    mutableMapOf<String, String?>().apply {
        loggingContext(this)
    },
    restorePrevious,
    body,
)
