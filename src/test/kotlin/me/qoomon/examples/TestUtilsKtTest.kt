package me.qoomon.examples

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.message

class TestUtilsKtTest {

    @Nested
    inner class JUnit {

        @Test
        fun dynamicTests() {
            // Given
            data class TestCase(val param: Int)

            val testDisplayName: TestCase.() -> String = { "$param" }
            val testMethod: TestCase.() -> Unit = mockk(relaxed = true)
            val testCases = {
                listOf(
                    TestCase(0),
                    TestCase(1),
                    TestCase(2)
                )
            }

            // When
            val dynamicTests = parameterizedTest(
                test = testMethod,
                testCases = testCases,
                displayName = testDisplayName
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

            verify(exactly = 3) { testMethod(any()) }
        }
    }

    @Nested
    inner class Strikt {

        @Test
        fun isFailure() {
            // Given
            val function = { throw Error("Boom!") }

            // When
            val result = runCatching(function)

            // Then
            expectThat(result).isFailure().and {
                isA<Error>()
                message.isEqualTo("Boom!")
            }
        }

        @Test
        fun isSuccess() {
            // Given
            val function = { "done" }

            // When
            val result = runCatching(function)

            // Then
            expectThat(result).isSuccess().and {
                isA<String>()
                isEqualTo("done")
            }
        }
    }
}
