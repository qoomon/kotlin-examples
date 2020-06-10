package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isFailure

class StrikeExtensionsTest {

    @Test
    internal fun `satisfies should pass if assertion returns true`() {
        // Given
        val value = "anna"

        // When
        expectThat(value) {
            satisfies("is a palindrome") {
                it == it.reversed()
            }
        }
    }

    @Test
    internal fun `satisfies should fail if assertion returns false`() {
        // Given
        val value = "tree"

        // When
        val result = runCatching {
            expectThat(value) {
                satisfies("is a palindrome") {
                    it == it.reversed()
                }
            }
        }

        // Then
        expectThat(result).isFailure().and {
            isA<AssertionError>()
        }
        println(result.exceptionOrNull())
    }
}
