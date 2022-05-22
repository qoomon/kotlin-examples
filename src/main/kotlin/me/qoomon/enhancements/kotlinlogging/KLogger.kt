package me.qoomon.enhancements.kotlinlogging

import mu.KLogger
import mu.withLoggingContext
import org.slf4j.Marker

// --- KLogger Lazy Context Extensions ---------------------------------------------------------------------------------

/**
 * Lazy add a log message if isTraceEnabled is true
 */
fun KLogger.trace(msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { trace(msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
fun KLogger.debug(msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(context.build()) { debug(msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled is true
 */
fun KLogger.info(msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(context.build()) { info(msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled is true
 */
fun KLogger.warn(msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(context.build()) { warn(msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled is true
 */
fun KLogger.error(msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(context.build()) { error(msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled is true
 */
fun KLogger.trace(t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { trace(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled is true
 */
fun KLogger.debug(t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(context.build()) { debug(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled is true
 */
fun KLogger.info(t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(context.build()) { info(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled is true
 */
fun KLogger.warn(t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(context.build()) { warn(t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled is true
 */
fun KLogger.error(t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(context.build()) { error(t, msg) }
    }
}

/**
 * Lazy add a log message if isTraceEnabled is true
 */
fun KLogger.trace(marker: Marker?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { trace(marker, msg) }
    }
}

/**
 * Lazy add a log message if isDebugEnabled is true
 */
fun KLogger.debug(marker: Marker?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(context.build()) { debug(marker, msg) }
    }
}

/**
 * Lazy add a log message if isInfoEnabled is true
 */
fun KLogger.info(marker: Marker?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(context.build()) { info(marker, msg) }
    }
}

/**
 * Lazy add a log message if isWarnEnabled is true
 */
fun KLogger.warn(marker: Marker?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(context.build()) { warn(marker, msg) }
    }
}

/**
 * Lazy add a log message if isErrorEnabled is true
 */
fun KLogger.error(marker: Marker?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(context.build()) { error(marker, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isTraceEnabled is true
 */
fun KLogger.trace(marker: Marker?, t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { trace(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isDebugEnabled is true
 */
fun KLogger.debug(marker: Marker?, t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isDebugEnabled) {
        withLoggingContext(context.build()) { debug(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isInfoEnabled is true
 */
fun KLogger.info(marker: Marker?, t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isInfoEnabled) {
        withLoggingContext(context.build()) { info(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isWarnEnabled is true
 */
fun KLogger.warn(marker: Marker?, t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isWarnEnabled) {
        withLoggingContext(context.build()) { warn(marker, t, msg) }
    }
}

/**
 * Lazy add a log message with throwable payload if isErrorEnabled is true
 */
fun KLogger.error(marker: Marker?, t: Throwable?, msg: () -> Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isErrorEnabled) {
        withLoggingContext(context.build()) { error(marker, t, msg) }
    }
}

/**
 * Add a log message with all the supplied parameters along with method name
 */
fun KLogger.entry(vararg argArray: Any?, context: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { entry(argArray) }
    }
}

/**
 * Add log message indicating exit of a method
 */
fun KLogger.exit(context: (MutableMap<String, String?>) -> Unit) {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { exit() }
    }
}

/**
 * Add a log message with the return value of a method
 */
fun <T> KLogger.exit(result: T, context: (MutableMap<String, String?>) -> Unit): T where T : Any? {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { exit(result) }
    }
    return result
}

/**
 * Add a log message indicating an exception will be thrown along with the stack trace.
 */
fun <T> KLogger.throwing(throwable: T, context: (MutableMap<String, String?>) -> Unit) where T : Throwable {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { throwing(throwable) }
    }
}

/**
 * Add a log message indicating an exception is caught along with the stack trace.
 */
fun <T> KLogger.catching(throwable: T, context: (MutableMap<String, String?>) -> Unit) where T : Throwable {
    if (isTraceEnabled) {
        withLoggingContext(context.build()) { catching(throwable) }
    }
}

private fun ((MutableMap<String, String?>) -> Unit).build(): MutableMap<String, String?> {
    val context = mutableMapOf<String, String?>()
    this(context)
    return context
}
