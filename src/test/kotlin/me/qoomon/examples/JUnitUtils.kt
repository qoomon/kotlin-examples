package me.qoomon.examples

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest

/**
 * **Usage example**
 *
 * ```
 * @TestFactory
 * fun `parameterizedTest example`() = parameterizedTest(
 *     test = {
 *         // Given: see Case
 *
 *         // When
 *         val result = givenBase.pow(givenExponent)
 *
 *         // Then
 *         Assert.assertThat(result, IsEqual(expectedResult))
 *     },
 *     displayName = { "$givenBase^$givenExponent should be $expectedResult" },
 *     cases = {
 *         data class Case(
 *             val givenBase: Double,
 *             val givenExponent: Double,
 *             val expectedResult: Double
 *         )
 *         listOf(
 *             Case(
 *                 givenBase = 2.0,
 *                 givenExponent = 2.0,
 *                 expectedResult = 4.0
 *             ),
 *             Case(
 *                 givenBase = 3.0,
 *                 givenExponent = 4.0,
 *                 expectedResult = 81.0
 *             )
 *         )
 *     }
 * )
 * ```
 */
fun <T> parameterizedTest(
    cases: () -> Collection<T>,
    displayName: T.(T) -> String = { toString() },
    test: T.(T) -> Unit
): List<DynamicTest> = cases().map { case ->
    dynamicTest(case.displayName(case)) {
        case.test(case)
    }
}
