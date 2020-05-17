package me.qoomon.examples

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.time.Instant
import java.time.LocalDateTime
import kotlin.time.days
import kotlin.time.hours
import kotlin.time.toJavaDuration

internal class TimeKtTest {

    val now = LocalDateTime.now()
    val tomorrow = now + 1.days.toJavaDuration()
    val yesterday = now - 1.days.toJavaDuration()

    @Test
    fun ago() {
        // Given
        val duration = 3.days

        // When
        val result = duration.ago(from = now)

        // Then
        val expectedResult = now - duration.toJavaDuration()
        expectThat(result) isEqualTo expectedResult
    }

    @Test
    fun withinLast() {
        // When
        val result = yesterday.withinLast(3.days)

        // Then
        expectThat(result).isTrue()
    }

    @Test
    fun `not withinLast`() {
        // When
        val result = yesterday.withinLast(1.hours)

        // Then
        expectThat(result).isFalse()
    }

    @Test
    fun ahead() {
        // Given
        val duration = 3.days

        // When
        val result = duration.ahead(from = now)

        // Then
        val expectedResult = now + duration.toJavaDuration()
        expectThat(result) isEqualTo expectedResult
    }

    @Test
    fun withinNext() {
        // When
        val result = tomorrow.withinNext(3.days)

        // Then
        expectThat(result).isTrue()
    }

    @Test
    fun `not withinNext`() {
        // When
        val result = tomorrow.withinNext(1.hours)

        // Then
        expectThat(result).isFalse()
    }
}
