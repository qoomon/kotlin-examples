package me.qoomon.examples

import kotlin.reflect.KClass
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
fun <T> retry(
    maxRetries: Int,
    delay: Duration = Duration.ZERO,
    catch: Set<KClass<out Throwable>> = setOf(Throwable::class),
    block: (Int) -> T,
): T {
    val suppressedExceptions = mutableListOf<Throwable>()
    for (i in 0 until maxRetries) {
        try {
            return block(i)
        } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
            if (catch.any { it.isInstance(exception) }) {
                suppressedExceptions.add(exception)
                Thread.sleep(delay.inWholeMilliseconds)
            } else {
                throw exception
            }
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

fun <T> tryAll(
    vararg attempts: () -> T,
    catch: Set<KClass<out Throwable>> = setOf(Throwable::class),
): T = tryAll(attempts.toList(), catch)

fun <T> tryAll(
    attempts: List<() -> T>,
    catch: Set<KClass<out Throwable>> = setOf(Throwable::class),
): T {
    val suppressedExceptions = mutableListOf<Throwable>()
    for (attempt in attempts) {
        try {
            return attempt()
        } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
            if (catch.any { it.isInstance(exception) }) {
                suppressedExceptions.add(exception)
            } else {
                throw exception
            }
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
