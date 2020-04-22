package me.qoomon.examples

import strikt.api.Assertion
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Asserts that the result of an action did throw an exception and maps to
 * an assertion on the exception. The assertion fails if the subject's
 * [Result.isFailure] returns `false`.
 */
fun <R> Assertion.Builder<Result<R>>.isFailure(): Assertion.Builder<Throwable> {
    return assert("is Failure") { result ->
        when {
            result.isFailure -> pass()
            else -> fail(
                description = "returned %s",
                actual = result.getOrThrow()
            )
        }
    }.get("exception") {
        exceptionOrNull()!!
    }
}

/**
 * Asserts that the result of an action did not throw any exception and maps to
 * an assertion on the result value. The assertion fails if the subject's
 * [Result.isSuccess] returns `false`.
 */
fun <R> Assertion.Builder<Result<R>>.isSuccess(): Assertion.Builder<R> {
    return assert("is Success") { result ->
        when {
            result.isSuccess -> pass()
            else -> fail(
                description = "threw %s",
                actual = result.exceptionOrNull(),
                cause = result.exceptionOrNull()
            )
        }
    }.get("value") {
        // WORKAROUND - Handle inline class bug. (This will also work when this bug is fixed)
        val value = getOrThrow()
        if (value is Result<*>)
            @Suppress("UNCHECKED_CAST")
            return@get value.getOrThrow() as R
        // WORKAROUND - END

        getOrThrow()
    }
}


/**
 * Asserts that the check execution did not throw any exception. The assertion fails
 * if execution throws an exception.
 */
fun <R> Assertion.Builder<R>.pass(check: R.(R) -> Unit): Assertion.Builder<R> {
    return assert("passes check") { value ->
        runCatching { value.check(value) }.apply {
            when {
                isSuccess -> pass()
                else -> {
                    fun Throwable.stackTraceAsString(): String {
                        val stringWriter = StringWriter()
                        this.printStackTrace(PrintWriter(stringWriter))
                        return stringWriter.toString()
                    }
                    fail(
                        description = "failure: %s%n"
                            + exceptionOrNull()!!.stackTraceAsString().trim().prependIndent(" "),
                        actual = exceptionOrNull()!!,
                        cause = exceptionOrNull()!!
                    )
                }
            }
        }
    }
}

