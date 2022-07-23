package me.qoomon.enhancements.kotlin

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class ReflectionsKtTest {

    @Test
    fun getBoxedValue() {
        // given
        val givenObject = Dummy("moin")

        // when
        val boxedValue = givenObject.boxedValue

        // then
        expectThat(boxedValue) isEqualTo givenObject.value
    }

    @Test
    fun getBoxedClass() {
        // given
        val givenObject = Dummy("moin")

        // when
        val boxedClass = givenObject::class.boxedClass

        // then
        expectThat(boxedClass) isEqualTo givenObject.value::class
    }

    @JvmInline
    value class Dummy(val value: String)
}
