package me.qoomon.examples

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.*

class TestUtilsKtTest {

    @Nested
    inner class JUnit {

        @Test
        fun dynamicTests() {
            // Given
            data class TestCase(val param: Int)

            val testDisplayName: TestCase.() -> String = { "$param" }
            val testMethod: TestCase.() -> Unit = mockk(relaxed = true)
            val testCases = listOf(
                TestCase(0),
                TestCase(1),
                TestCase(2))

            // When
            val dynamicTests = dynamicTests(testDisplayName, testMethod) {
                testCases
            }

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

        @Test
        fun expectPass() {
            // Given
            val assertion = { assert(false) { "Boom!" } }

            // When
            val result = runCatching {
                expectPass(assertion)
            }

            // Then
            expectThat(result).isFailure().and {
                isA<AssertionFailedError>()
                cause.isA<AssertionError>()
                cause.get { this?.message }.isEqualTo("Boom!")
            }
        }
    }
}
