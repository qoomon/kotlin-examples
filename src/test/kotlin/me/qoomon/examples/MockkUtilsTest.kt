package me.qoomon.examples

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.time.Duration
import kotlin.time.days

internal class MockkUtilsTest {

    @Test
    internal fun name() {
        // Given
        val givenString = "3"
        val givenDuration = 3.days

        // When
        val dummy = mockk<Dummy> {
            every { functionWithStringParameter(anyValue()) } returns givenString
            every { functionWithDurationParameter(anyValue()) } returns value(givenDuration)
        }

        // Then
        val stringResult = dummy.functionWithStringParameter("")
        expectThat(stringResult).isEqualTo(givenString)
        val durationResult = dummy.functionWithDurationParameter(0.days)
        expectThat(durationResult).isEqualTo(givenDuration)
    }

    private class Dummy {
        fun functionWithStringParameter(param: String) = param
        fun functionWithDurationParameter(param: Duration) = param
    }
}
