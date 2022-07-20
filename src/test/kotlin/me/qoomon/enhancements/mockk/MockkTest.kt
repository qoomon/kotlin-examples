package me.qoomon.enhancements.mockk

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class MockkTest {

    @Test
    fun `any matcher for value class`() {
        // given
        val mock = mockk<ValueServiceDummy>(relaxed = true)
        val givenResult = 1
        every { mock.doSomething(anyValue()) } returns givenResult

        // when
        val result = mock.doSomething(ValueDummy("moin"))

        // then
        expectThat(result).isEqualTo(givenResult)
    }

    @Test
    fun `slot for value class`() {
        // given
        val mock = mockk<ValueServiceDummy>(relaxed = true)
        val slot = slot<ValueDummy>()
        val givenResult = 1
        every { mock.doSomething(captureValue(slot)) } returns givenResult

        val givenParameter = ValueDummy("s")

        // when
        val result = mock.doSomething(givenParameter)

        // then
        expectThat(result).isEqualTo(givenResult)
        expectThat(slot.captured).isEqualTo(givenParameter)
    }

    @Test
    fun `value class as return value`() {
        // given
        val mock = mockk<ValueServiceDummy>(relaxed = true)
        val givenResult = ValueDummy("moin")
        every { mock.getSomething() } returns givenResult

        // when
        val result = mock.getSomething()

        // then
        expectThat(result).isEqualTo(givenResult)
    }
}

@JvmInline
value class ValueDummy(val value: String)

interface ValueServiceDummy {
    fun doSomething(value: ValueDummy): Int
    fun getSomething(): ValueDummy
}
