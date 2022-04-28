package me.qoomon.examples

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.time.Duration.Companion.seconds

class TimerTest {

    @Test
    fun isFailure() {
        // Given
        val period = 2.seconds
        val duration = 4.seconds

        // When
        var count = 0L

        runBlocking {
            schedule(period) {
                count++
            }
            delay(duration)
        }

        // Then
        val expectedCount = duration.inWholeSeconds / period.inWholeSeconds
        expectThat(count).isEqualTo(expectedCount)
    }
}
