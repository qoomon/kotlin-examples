package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.*

class StriktTest {

    @Test
    fun isFailure() {
        // Given
        val function = { throw Error("Boom!") }

        // When
        val result = runCatching { function() }

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
        val result = runCatching { function() }

        // Then
        expectThat(result).isSuccess().and {
            isA<String>()
            isEqualTo("done")
        }
    }
<<<<<<< Updated upstream
||||||| constructed merge base

    @Test
    fun `expectThat satisfy`() {
        expectThat("foo") {
            pass { assert(it == "foo") { "name is wrong" } }
        }
    }

    @Test
    fun `expect that satisfy`() {
        expect {
            that("foo") {
                pass { assert(it == "foo") { "name is wrong" } }
            }
        }
    }


=======

    @Test
    fun `expectThat satisfy`() {
        expectThat("foo") {
            satisfies { assert(it == "foo") { "name is wrong" } }
        }
    }

    @Test
    fun `expect that satisfy`() {
        expect {
            that("foo") {
                satisfies { assert(it == "foo") { "name is wrong" } }
            }
        }
    }
>>>>>>> Stashed changes
}
