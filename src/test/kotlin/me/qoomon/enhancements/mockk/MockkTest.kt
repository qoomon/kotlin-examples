package me.qoomon.enhancements.mockk

import io.ktor.util.reflect.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFailure
import java.util.*
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

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
    fun `return Result`() {
        val givenValue = Result.success(1)
        val service = mockk<DummyService> {
            every { doSomething() } returns givenValue
        }

        val result = service.doSomething()

        assertEquals(givenValue, result)
    }

    @Test
    fun `return Duration`() {
        val givenValue = 1.hours
        val service = mockk<DummyService> {
            every { getDuration() } returns givenValue
        }

        val result = service.getDuration()

        assertEquals(givenValue, result)
    }

    @Test
    fun `enum 1`() {
        val repo = mockk<DummyService>()
        every { repo.getSchedules() } returns "Foo"

        val result = repo.getSchedules()

        assertEquals("Foo", result)
    }

    @Test
    fun `enum 2`() {
        val repo = mockk<DummyService>()
        every { repo.getEnumerations() } returns Collections.enumeration(listOf("a", "b", "c"))

        val result = repo.getEnumerations()

        assertEquals(listOf("a", "b", "c"), result.toList())
    }

    data class Dummy(val foo: String, val bar: Int)

    private interface DummyService {
        fun doSomething(): Result<Int>
        fun getDuration(): Duration
        fun getSchedules(sortOrder: DummyEnum = DummyEnum.ASC): String
        fun getEnumerations(): Enumeration<String>
    }

    enum class DummyEnum {
        ASC,
        DESC,
    }
}
