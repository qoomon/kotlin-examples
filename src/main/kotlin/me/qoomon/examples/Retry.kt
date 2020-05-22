package me.qoomon.examples

import kotlin.time.Duration

/**
 * ```
 * val result = retry(5, delay = 2.seconds) {
 *   println("try: $it")
 *   if (it < 3) throw Exception("$it")
 *   "success $it"
 * }
 * println("result: $result")
 * ```
 */
fun <T> retry(maxReties: Int, delay: Duration = Duration.ZERO, block: (Int) -> T): T {
    val suppressedExceptions = mutableListOf<Throwable>()
    for (i in 0 until maxReties) {
        try {
            return block(i)
        } catch (exception: Throwable) {
            suppressedExceptions.add(exception)
            Thread.sleep(delay.toLongMilliseconds())
        }
    }
    suppressedExceptions.reverse()
    val recentError = suppressedExceptions.removeAt(0)
    throw recentError.apply {
        suppressedExceptions.forEach {
            addSuppressed(it)
        }
    }
}

fun <T> tryAll(vararg attempts: () -> T): T = tryAll(attempts.toList())
fun <T> tryAll(attempts: List<() -> T>): T {
    val suppressedExceptions = mutableListOf<Throwable>()
    for (attempt in attempts) {
        try {
            return attempt()
        } catch (exception: Throwable) {
            suppressedExceptions.add(exception)
        }
    }
    suppressedExceptions.reverse()
    val recentError = suppressedExceptions.removeAt(0)
    throw recentError.apply {
        suppressedExceptions.forEach {
            addSuppressed(it)
        }
    }
}
