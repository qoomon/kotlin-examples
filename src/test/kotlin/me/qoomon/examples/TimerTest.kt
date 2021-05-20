package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.time.Duration.Companion.seconds

class TimerTest {

    @Test
    fun isFailure() {
        // Given
        val period = seconds(2)
        val duration = seconds(10)

        // When
        var count = 0

        schedule(period) {
            count++
        }
        Thread.sleep(duration.inWholeMilliseconds)

        // Then
        val expectedCount = duration.inWholeSeconds.toInt() / period.inWholeSeconds.toInt()
        expectThat(count).isEqualTo(expectedCount)
    }
}
