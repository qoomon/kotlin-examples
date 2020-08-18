package me.qoomon.examples

import org.junit.jupiter.api.Test
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
}
