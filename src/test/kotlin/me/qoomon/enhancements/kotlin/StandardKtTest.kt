package me.qoomon.enhancements.kotlin

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEqualTo

internal class StandardKtTest {

    @Test
    fun `applyIf do apply if predicate is true`() {
        // Given
        val subject = mutableListOf<String>()

        // When
        val result = subject.applyIf({ size == 0 }) {
            add("moin")
        }
        // Then
        expectThat(result).containsExactly("moin")
    }

    @Test
    fun `applyIf do not apply if predicate is false`() {
        // Given
        val subject = mutableListOf("hallo")

        // When
        val result = subject.applyIf({ size == 0 }) {
            add("moin")
        }

        // Then
        expectThat(result).containsExactly("hallo")
    }

    @Test
    fun `letIf do let if predicate is true`() {
        // Given
        val subject = "Hello"

        // When
        val result = subject.letIf({ length > 0 }) {
            "$this World"
        }
        // Then
        expectThat(result).isEqualTo("Hello World")
    }

    @Test
    fun `letIf do not let if predicate is false`() {
        // Given
        val subject = "Hello"

        // When
        val result = subject.letIf({ length == 0 }) {
            "$this World"
        }

        // Then
        expectThat(result).isEqualTo("Hello")
    }
}
