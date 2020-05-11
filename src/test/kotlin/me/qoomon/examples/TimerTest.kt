package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.time.seconds

class TimerTest {

    @Test
    fun isFailure() {
        // Given
        val period = 2.seconds
        val duration = 10.seconds

        // When
        var count = 0

        schedule(period) {
            count++
        }
        Thread.sleep(duration.toLongMilliseconds())

        // Then
        val expectedCount = duration.inSeconds.toInt() / period.inSeconds.toInt()
        expectThat(count).isEqualTo(expectedCount)
    }
}
