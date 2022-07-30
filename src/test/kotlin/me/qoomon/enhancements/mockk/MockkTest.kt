package me.qoomon.enhancements.mockk

import io.kotest.matchers.types.shouldBeTypeOf
import io.ktor.util.reflect.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.isFailure
import kotlin.test.assertEquals

class MockkTest {

    @Test
    fun `verifyAllPropertyGetters - success`() {
        val dummy = spyk(Dummy(foo = "123", bar = 73))

        dummy.foo
        dummy.bar

        verifyAllPropertyGetters(dummy)
    }

    @Test
    fun `verifyAllPropertyGetters - success with excluded property`() {
        val dummy = spyk(Dummy(foo = "123", bar = 73))

        dummy.bar

        verifyAllPropertyGetters(dummy, except = arrayOf(Dummy::foo))
    }

    @Test
    fun `verifyAllPropertyGetters - failure`() {
        val dummy = spyk(Dummy(foo = "123", bar = 73))

        dummy.foo

        val result = kotlin.runCatching { verifyAllPropertyGetters(dummy) }

        expectThat(result).isFailure().instanceOf(AssertionError::class)
    }

    @Test
    fun `result value`() {
        val givenValue = Result.success(1)
        val service = mockk<DummyService> {
            every { doSomething() } returns givenValue
        }

        val result = service.doSomething()

        assertEquals(givenValue, result)
    }

    data class Dummy(val foo: String, val bar: Int)

    private interface DummyService {
        fun doSomething() : Result<Int>
    }


}


