package me.qoomon.enhancements.kotlin

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class Base64KtTest {

    @Test
    fun decodeBase64() {
        // Given
        val givenString = "bW9pbg=="

        // When
        val result = givenString.decodeBase64()

        // Then
        expectThat(result) isEqualTo "moin"
    }

    @Test
    fun encodeBase64() {
        // Given
        val givenString = "moin"

        // When
        val result = givenString.encodeBase64()

        // Then
        expectThat(result) isEqualTo "bW9pbg=="
    }
}
