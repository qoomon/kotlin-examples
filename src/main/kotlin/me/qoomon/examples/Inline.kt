package me.qoomon.examples

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

interface Value<T> {
    val value: T
}

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class DummyString internal constructor(override val value: String) : Value<String> {
    companion object {
        fun of(value: String): DummyString {
            require(value.length <= 32) { "length is greater than 32" }
            return DummyString(value)
        }
    }
}

fun String.asDummyString() = DummyString.of(this)

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class DummyInt internal constructor(override val value: Int) : Value<Int> {
    companion object {
        fun of(value: Int): DummyInt {
            require(value <= 64) { "is less or equals than 64" }
            return DummyInt(value)
        }
    }
}

fun Int.asDummyInt() = DummyInt.of(this)

fun main() {
    "John".asDummyString()
    2.asDummyInt()
}

val KClass<*>.isInline: Boolean
    get() = !isData &&
        primaryConstructor?.parameters?.size == 1 &&
        java.declaredMethods.any { it.name == "box-impl" }
