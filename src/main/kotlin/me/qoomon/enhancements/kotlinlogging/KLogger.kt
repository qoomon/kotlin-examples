package me.qoomon.enhancements.kotlinlogging

import mu.KLogger
import mu.withLoggingContext
import org.slf4j.Marker

typealias LoggingContext = MutableMap<String, String?>

// --- KLogger Lazy Context Extensions ---------------------------------------------------------------------------------

/**
 * Lazy add a log message if isTraceEnabled is true
 */
fun KLogger.trace(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { trace(msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
fun KLogger.debug(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isDebugEnabled) {
        withLoggingContext(loggingContext) { debug(msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled is true
 */
fun KLogger.info(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isInfoEnabled) {
        withLoggingContext(loggingContext) { info(msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled is true
 */
fun KLogger.warn(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isWarnEnabled) {
        withLoggingContext(loggingContext) { warn(msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled is true
 */
fun KLogger.error(
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isErrorEnabled) {
        withLoggingContext(loggingContext) { error(msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled is true
 */
fun KLogger.trace(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { trace(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled is true
 */
fun KLogger.debug(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isDebugEnabled) {
        withLoggingContext(loggingContext) { debug(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled is true
 */
fun KLogger.info(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isInfoEnabled) {
        withLoggingContext(loggingContext) { info(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled is true
 */
fun KLogger.warn(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isWarnEnabled) {
        withLoggingContext(loggingContext) { warn(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled is true
 */
fun KLogger.error(
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isErrorEnabled) {
        withLoggingContext(loggingContext) { error(t, msg) }
    }
}

/**
 * Lazy add a log message if isTraceEnabled is true
 */
fun KLogger.trace(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { trace(marker, msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
fun KLogger.debug(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isDebugEnabled) {
        withLoggingContext(loggingContext) { debug(marker, msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled is true
 */
fun KLogger.info(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isInfoEnabled) {
        withLoggingContext(loggingContext) { info(marker, msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled is true
 */
fun KLogger.warn(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isWarnEnabled) {
        withLoggingContext(loggingContext) { warn(marker, msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled is true
 */
fun KLogger.error(
    marker: Marker?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isErrorEnabled) {
        withLoggingContext(loggingContext) { error(marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled is true
 */
fun KLogger.trace(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { trace(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled is true
 */
fun KLogger.debug(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isDebugEnabled) {
        withLoggingContext(loggingContext) { debug(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled is true
 */
fun KLogger.info(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isInfoEnabled) {
        withLoggingContext(loggingContext) { info(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled is true
 */
fun KLogger.warn(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isWarnEnabled) {
        withLoggingContext(loggingContext) { warn(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled is true
 */
fun KLogger.error(
    marker: Marker?,
    t: Throwable?,
    msg: () -> Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isErrorEnabled) {
        withLoggingContext(loggingContext) { error(marker, t, msg) }
    }
}

/**
 * Add a log message with all the supplied parameters along with method name
 */
fun KLogger.entry(
    vararg argArray: Any?,
    loggingContext: (LoggingContext) -> Unit
) {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { entry(argArray) }
    }
}

/**
 * Add log message indicating exit of a method
 */
fun KLogger.exit(loggingContext: (LoggingContext) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { exit() }
    }
}

/**
 * Add a log message with the return value of a method
 */
fun <T> KLogger.exit(result: T, loggingContext: (LoggingContext) -> Unit): T where T : Any? {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { exit(result) }
    }
    return result
}

/**
 * Add a log message indicating an exception will be thrown along with the stack trace.
 */
fun <T> KLogger.throwing(throwable: T, loggingContext: (LoggingContext) -> Unit) where T : Throwable {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { throwing(throwable) }
    }
}

/**
 * Add a log message indicating an exception is caught along with the stack trace.
 */
fun <T> KLogger.catching(throwable: T, loggingContext: (LoggingContext) -> Unit) where T : Throwable {
    if (isTraceEnabled) {
        withLoggingContext(loggingContext) { catching(throwable) }
    }
}

inline fun <T> withLoggingContext(
    loggingContext: (LoggingContext) -> Unit,
    restorePrevious: Boolean = true,
    body: () -> T
) = withLoggingContext(mutableMapOf<String, String?>().apply { loggingContext(this) }, restorePrevious, body)
