package me.qoomon.examples

import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import kotlin.math.pow

class JUnitUtilsTest {

    @Test
    fun parameterizedTest() {
        // Given
        data class TestCase(val param: Int)

        val testDisplayName: TestCase.(TestCase) -> String = { "$param" }
        val testMethod: TestCase.(TestCase) -> Unit = mockk(relaxed = true)
        val testCases = {
            listOf(
                TestCase(0),
                TestCase(1),
                TestCase(2)
            )
        }

        // When
        val dynamicTests = parameterizedTest(
            cases = testCases,
            displayName = testDisplayName,
            test = testMethod
        )

        dynamicTests.forEach {
            it.executable.execute()
        }

        // Then
        expectThat(dynamicTests).hasSize(3)
        expect {
            that(dynamicTests[0]) {
                get { displayName }.isEqualTo("0")
            }
            that(dynamicTests[1]) {
                get { displayName }.isEqualTo("1")
            }
            that(dynamicTests[2]) {
                get { displayName }.isEqualTo("2")
            }
        }

        verify(exactly = 3) { testMethod(any(), any()) }
    }

    @TestFactory
    fun `parameterizedTest example`() = parameterizedTest(
        test = {
            // Given: see Case

            // When
            val result = givenBase.pow(givenExponent)

            // Then
            assertThat(result, IsEqual(expectedResult))
        },
        displayName = { "$givenBase^$givenExponent should be $expectedResult" },
        cases = {
            data class Case(
                val givenBase: Double,
                val givenExponent: Double,
                val expectedResult: Double
            )
            listOf(
                Case(
                    givenBase = 2.0,
                    givenExponent = 2.0,
                    expectedResult = 4.0
                ),
                Case(
                    givenBase = 3.0,
                    givenExponent = 4.0,
                    expectedResult = 81.0
                )
            )
        }
    )
}
