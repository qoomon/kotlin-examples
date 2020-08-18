package me.qoomon.examples

import strikt.api.Assertion

/**
 * Asserts that the subject satisfies a given condition.
 *
 * @param description a description for the condition the assertion evaluates.
 * @param condition the condition that should result true if subject satisfies condition else false.
 * @return this assertion builder, in order to facilitate a fluent API.
 */
fun <T> Assertion.Builder<T>.satisfies(description: String, condition: (T) -> Boolean): Assertion.Builder<T> {
    return assert(description) { if (condition(it)) pass() else fail() }
}

/**
 * Asserts that a string is a palindrome.
 */
fun Assertion.Builder<String>.isAPalindrome(): Assertion.Builder<String> {
    return assert("is a palindrome") { value ->
        if (value == value.reversed()) pass() else fail(
            description = "is not a palindrome",
            actual = value
        )
    }
}


