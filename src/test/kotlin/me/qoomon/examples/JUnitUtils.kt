package me.qoomon.examples

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

fun <T> parameterizedTest(
    cases: () -> Collection<T>,
    displayName: T.(T) -> String = { toString() },
    test: T.(T) -> Unit
): List<DynamicTest> {
    return cases().map { parameters ->
        dynamicTest(displayName(parameters, parameters)) {
            parameters.test(parameters)
        }
    }
}
