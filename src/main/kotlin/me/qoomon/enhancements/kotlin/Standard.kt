package me.qoomon.enhancements.kotlin

import io.ktor.utils.io.CancellationException
import kotlin.reflect.KClass

inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> Unit): T = apply {
    if (predicate(this)) block(this)
}

inline fun <T> T.letIf(predicate: T.() -> Boolean, block: T.() -> T): T = let {
    if (predicate(this)) block(this) else it
}

inline fun <reified E, T> Result<T>.rethrow(): Result<T> {
    if (isFailure) {
        val exception = exceptionOrNull()!!
        if (exception is E) throw exception
    }
    return this
}

inline fun <R> runCatchingExcept(vararg except: KClass<out Throwable>, block: () -> R): Result<R> =
    runCatching(block).applyIf({ isFailure }) {
        exceptionOrNull()!!.apply { if (except.any { it.isInstance(this) }) throw this }
    }
