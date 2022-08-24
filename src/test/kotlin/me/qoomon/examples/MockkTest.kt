package me.qoomon.examples

import io.mockk.*
import org.junit.Ignore
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.test.assertEquals

// @MockKExtension.ConfirmVerification
class MockkTest {

    data class Foo(val value: String)

    interface Factory {

        fun get(): String
        fun getFoo(): Foo
    }

    @Test
    fun `multiple answers`() {
        val factory = mockk<Factory>()
        every { factory.get() } returnsMany listOf("a", "b")
    }

    @Test
    fun `factory return mock`() {
        mockk<Factory> {
            every { getFoo() } returns mockk {
                every { value } returns "foo"
            }
        }
    }

    @Test
    fun `any matcher for value class`() {
        // given
        val mock = mockk<ServiceDummy>(relaxed = true)
        val givenResult = ValueDummy("given")
        every { mock.doSomething(any()) } returns givenResult

        // when
        val result = mock.doSomething(ValueDummy("moin"))

        // then
        expectThat(result).isEqualTo(givenResult)
    }

    @Test
    fun `slot for value class`() {
        // given
        val mock = mockk<ServiceDummy>(relaxed = true)
        val slot = slot<ValueDummy>()
        val givenResult = ValueDummy("given")
        every { mock.doSomething(capture(slot)) } returns givenResult

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
        val mock = mockk<ServiceDummy>(relaxed = true)
        val givenResult = ValueDummy("moin")
        every { mock.getSomething() } returns givenResult

        // when
        val result = mock.getSomething()

        // then
        expectThat(result).isEqualTo(givenResult)
    }

    @Test
    @Ignore("not supported yet")
    fun `mock extension blocks`() {
        // Given
        val dummy = mockk<ServiceDummy> {
            every { transaction(any<ServiceDummy.() -> String>()) } answers {
                val block = arg<ServiceDummy.() -> String>(0)
                this@mockk.block()
            }
            every { getSomething() } returns ValueDummy("mock data")
        }

        // When
        val data = dummy.transaction {
            getSomething()
        }

        // Then
        expectThat(data).isEqualTo(ValueDummy("mock data"))

        verify { dummy.transaction(any()) }
        verify { dummy.getSomething() }
        confirmVerified()
    }

    @Test
    fun `mock extension blocks simple`() {
        val givenValue = 42
        val dummy = mockk<ServiceDummy> {
            every { execute(any()) } answers {
                val block = arg<ServiceDummy.() -> Int>(0)
                this@mockk.block()
            }
        }

        val result = dummy.execute { givenValue }

        assertEquals(givenValue, result)
        verify { dummy.execute(any()) }
    }

    @Test
    fun `slot test`() {
        val slot = listSlot<ValueDummy>()
        val dummy = mockk<ServiceDummy> {
            every { doSomething(capture(slot)) } returns ValueDummy("given")
        }
        dummy.doSomething(ValueDummy("a"))
        dummy.doSomething(ValueDummy("b"))
        dummy.doSomething(ValueDummy("c"))

        println(slot.captured)
        println(slot)
    }

    interface ResultServiceDummy {
        fun getAny(): Any
        fun getResult(): Result<Int>
    }

    @Test
    fun `return Result for function with Result return type`() {
        val givenValue = Result.success(42)
        val service = mockk<ResultServiceDummy> {
            every { getResult() } returns givenValue
        }

        val result = service.getResult()

        assertEquals(givenValue, result)
    }

    @Test
    @Ignore("not supported yet")
    fun `return Result for function with Any return type`() {
        val givenValue = Result.success(42)
        val service = mockk<ResultServiceDummy> {
            every { getAny() } returns givenValue
        }

        val result = service.getAny()

        assertEquals(givenValue, result)
    }

    @JvmInline
    value class ValueDummy(val value: String)

    interface ServiceDummy {
        fun <T> transaction(block: ServiceDummy.() -> T) = block()
        fun execute(block: ServiceDummy.() -> Int) = block()

        fun getSomething(): ValueDummy
        fun doSomething(value: ValueDummy): ValueDummy
    }
}

inline fun <reified T : Any> listSlot() = ListCapturingSlot<T>()

class ListCapturingSlot<T : Any> private constructor(private val captureList: MutableList<T>) :
    MutableList<T> by captureList {
    constructor() : this(mutableListOf())

    val captured: T
        get() = captureList.last()

    override fun toString(): String =
        if (captureList.isEmpty()) "slot()"
        else captureList.joinToString("\n") { "slot($it)" }
}
