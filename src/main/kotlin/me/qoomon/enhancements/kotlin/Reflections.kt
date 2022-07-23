package me.qoomon.enhancements.kotlin

import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Underlying property value of a **`value class`** or self
 */
val <T : Any> T.boxedValue: Any?
    @Suppress("UNCHECKED_CAST")
    get() = if (!this::class.isValue) this
    else (this::class as KClass<T>).boxedProperty.get(this)

/**
 * Underlying property class of a **`value class`** or self
 */
val KClass<*>.boxedClass: KClass<*>
    get() = if (!this.isValue) this
    else this.boxedProperty.returnType.classifier as KClass<*>

/**
 * Underlying property of a **`value class`**
 */
private val <T : Any> KClass<T>.boxedProperty: KProperty1<T, *>
    get() = if (!this.isValue) throw IllegalArgumentException("$this is not a value class")
    // value classes always have exactly one property
    else this.declaredMemberProperties.first().apply { isAccessible = true }
