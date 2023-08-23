package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess
import strikt.assertions.message

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
}
