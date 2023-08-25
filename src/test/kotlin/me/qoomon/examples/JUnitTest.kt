package me.qoomon.examples

import me.qoomon.enhancements.junit.parameterizedTest
import org.junit.jupiter.api.TestFactory
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JUnitTest {

    @TestFactory
    fun `parameterizedTest example`() = parameterizedTest({
        data class Case(val name: String)
        listOf(
            Case("John"),
            Case("Peter"),
        )
    }) {
        // given
        val greeting = "Hello"

        // when
        val result = "$greeting $name"

        // then
        expectThat(result).isEqualTo("$greeting $name")
    }

    @TestFactory
    fun `parameterizedTest example 2`() = parameterizedTest(
        test = { case ->
            // given
            val greeting = "Hello"

            // when
            val result = "$greeting ${case.name}"

            // then
            expectThat(result).isEqualTo("$greeting ${case.name}")
        },
        cases = {
            data class Case(val name: String)
            listOf(
                Case("John"),
                Case("Peter"),
            )
        },
    )
}
