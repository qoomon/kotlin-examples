package me.qoomon.examples

import strikt.api.Assertion
import java.io.PrintWriter
import java.io.StringWriter

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
                        description = "failure: %s%n" +
                            exceptionOrNull()!!.stackTraceAsString().trim().prependIndent(" "),
                        actual = exceptionOrNull()!!,
                        cause = exceptionOrNull()!!
                    )
                }
            }
        }
    }
}
