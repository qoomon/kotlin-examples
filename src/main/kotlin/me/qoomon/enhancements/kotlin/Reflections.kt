package me.qoomon.enhancements.kotlin

import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.internal.impl.load.kotlin.header.KotlinClassHeader
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

val KClass<*>.isKotlin: Boolean
    /** @see [KotlinClassHeader.Kind] */
    get() = this.findAnnotation<Metadata>()?.kind == 1

/** Underlying property value of a **`value class`** or self */
val <T : Any> T.boxedValue: Any?
    @Suppress("UNCHECKED_CAST")
    get() = if (!this::class.isValue) this
    else (this::class as KClass<T>).boxedProperty.get(this)

/** Underlying property class of a **`value class`** or self */
val KClass<*>.boxedClass: KClass<*>
    get() = if (!this.isValue) this
    else this.boxedProperty.returnType.classifier as KClass<*>

val <T : Any> KClass<T>.declaredFieldProperties: Collection<KProperty1<T, *>>
    get() = this.declaredMemberProperties.filter { it.javaField != null }

/** Underlying property of a **`value class` */
private val <T : Any> KClass<T>.boxedProperty: KProperty1<T, *>
    get() = if (!this.isValue) throw UnsupportedOperationException("$this is not a value class")
    // value classes always have exactly one property with a backing field
    else this.declaredFieldProperties.first().apply { isAccessible = true }

private val <T : Any> KClass<T>.isValue_safe: Boolean
    get() = try {
        isValue
    } catch (_: UnsupportedOperationException) {
        false
    }
