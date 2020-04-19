package me.qoomon.examples

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

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

        verify(exactly = 3) { testMethod(any(),any()) }
    }

}
