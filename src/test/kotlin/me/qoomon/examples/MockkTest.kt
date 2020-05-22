package me.qoomon.examples

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

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

        val factory = mockk<Factory> {
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
    }

    private class Dummy {
        fun <T> transaction(block: Dummy.() -> T) = block()
        fun getData() = "real data"
    }
}



