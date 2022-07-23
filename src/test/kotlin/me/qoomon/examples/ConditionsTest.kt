package me.qoomon.examples

import me.qoomon.enhancements.kotlin.and
import me.qoomon.enhancements.kotlin.or
import me.qoomon.enhancements.kotlin.xor
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

internal class ConditionsTest {

    @Test
    fun and() {
        // When
        val result = ({ true } and { false })()

        // Then
        expectThat(result).isFalse()
    }

    @Test
    fun or() {
        // When
        val result = ({ false } or { true })()

        // Then
        expectThat(result).isTrue()
    }

    @Test
    fun xor() {
        // When
        val result = ({ true } xor { true })()

        // Then
        expectThat(result).isFalse()
    }
}
