package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import java.time.Instant
import kotlin.time.days
import kotlin.time.hours
import kotlin.time.toJavaDuration

internal class TimeTest {

    val now = Instant.now()
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
        val result = yesterday.isWithinLast(3.days)

        // Then
        expectThat(result).isTrue()
    }

    @Test
    fun `not withinLast`() {
        // When
        val result = yesterday.isWithinLast(1.hours)

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
        val result = tomorrow.isWithinNext(3.days)

        // Then
        expectThat(result).isTrue()
    }

    @Test
    fun `not withinNext`() {
        // When
        val result = tomorrow.isWithinNext(1.hours)

        // Then
        expectThat(result).isFalse()
    }
}
