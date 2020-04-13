package me.qoomon.examples

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import strikt.api.Assertion

fun <T> parameterizedTest(
    test: T.() -> Unit,
    testCases: () -> Collection<T>,
    displayName: T.() -> String = { this.toString() }
): List<DynamicTest> {
    return testCases().map { parameters ->
        dynamicTest(displayName(parameters)) {
            test(parameters)
        }
    }
}

/**
 * Asserts that the result of an action did throw an exception and maps to
 * an assertion on the exception. The assertion fails if the subject's
 * [Result.isFailure] returns `false`.
 */
fun <R> Assertion.Builder<Result<R>>.isFailure(): Assertion.Builder<Throwable> {
    return assert("is Failure") {
        when {
            it.isFailure -> pass()
            else -> fail(
                description = "returned %s",
                actual = it.getOrThrow()
            )
        }
    }
        .get("exception") {
            exceptionOrNull()!!
        }
}

/**
 * Asserts that the result of an action did not throw any exception and maps to
 * an assertion on the result value. The assertion fails if the subject's
 * [Result.isSuccess] returns `false`.
 */
fun <R> Assertion.Builder<Result<R>>.isSuccess(): Assertion.Builder<R> {
    return assert("is Success") {
        when {
            it.isSuccess -> pass()
            else -> fail(
                description = "threw %s",
                actual = it.exceptionOrNull(),
                cause = it.exceptionOrNull()
            )
        }
    }
        .get("value") {
            // WORKAROUND - Handle inline class bug. (This will also work when this bug is fixed)
            val value = getOrThrow()
            if (value is Result<*>)
                @Suppress("UNCHECKED_CAST")
                return@get value.getOrThrow() as R
            // WORKAROUND - END

            getOrThrow()
        }
}
