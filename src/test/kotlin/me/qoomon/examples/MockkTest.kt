package me.qoomon.examples

import io.mockk.*
import io.mockk.junit5.MockKExtension
import me.qoomon.enhancements.mockk.anyValue
import me.qoomon.enhancements.mockk.captureValue
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@MockKExtension.ConfirmVerification
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
    fun `mock extension blocks`() {
        // Given
        val dummy = mockk<Dummy>()

        dummy.apply {
            every { transaction(any<Dummy.() -> Any>()) } answers {
                val block = arg<Dummy.() -> Any>(0)
                this@apply.block()
            }
            every { getData() } returns "mock data"
        }

        // When
        val data = dummy.transaction {
            getData()
        }

        // Then
        expectThat(data).isEqualTo("mock data")
        confirmVerified()
    }

    @Test
    fun `slot test`() {
        val slot = listSlot<String>()
        val dummy = mockk<Dummy> {
            every { setData(capture(slot)) } just Runs
        }
        dummy.setData("a")
        dummy.setData("b")
        dummy.setData("c")

        println(slot.captured)
        println(slot)
    }

    @Test
    fun `mockk for any value class parameters`() {
        val mock = mockk<ValueServiceDummy>(relaxed = true)
        every { mock.doSomething(anyValue()) } returns 1

        mock.doSomething(ValueDummy("s"))
    }

    @Test
    fun `mockk with value class slot`() {

        val slot = slot<ValueDummy>()
        val mock = mockk<ValueServiceDummy>(relaxed = true)
        every { mock.doSomething(captureValue(slot)) } returns 1

        val callParameter = ValueDummy("s")

        mock.doSomething(callParameter)

        expectThat(slot.captured).isEqualTo(callParameter)
    }

    private class Dummy {
        fun <T> transaction(block: Dummy.() -> T) = block()
        fun getData() = "real data"
        fun setData(value: String) {}
    }

    @JvmInline
    value class ValueDummy(val value: String)

    interface ValueServiceDummy {
        fun doSomething(value: ValueDummy): Int
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
