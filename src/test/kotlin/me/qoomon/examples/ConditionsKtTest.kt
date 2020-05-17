package me.qoomon.examples

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

internal class ConditionsKtTest {

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
