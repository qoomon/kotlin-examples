package me.qoomon.examples

import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
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
    fun `slot`() {
        val slot = slot<String>()
        val dummy = mockk<Dummy> {
            every { setData(capture(slot)) } just Runs
        }
        dummy.setData("a")
        dummy.setData("b")
        dummy.setData("c")

        println(slot.captured)
        println(slot)
    }

    private class Dummy {
        fun <T> transaction(block: Dummy.() -> T) = block()
        fun getData() = "real data"
        fun setData(value: String) {}
    }
}

inline fun <reified T : Any> slot() = ListCapturingSlot<T>()

class ListCapturingSlot<T : Any> private constructor(private val captureList: MutableList<T>) :
    MutableList<T> by captureList {
    constructor() : this(mutableListOf())

    val captured: T
        get() = captureList.last()

    override fun toString(): String =
        if (captureList.isEmpty()) "slot()"
        else captureList.joinToString("\n") { "slot($it)" }
}
