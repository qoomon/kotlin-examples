package me.qoomon.examples

import io.mockk.ConstantMatcher
import io.mockk.Matcher
import io.mockk.MockKGateway
import io.mockk.MockKMatcherScope
import kotlin.reflect.KClass

import kotlin.reflect.full.primaryConstructor
import kotlin.time.Duration

inline fun <reified T : Any> value(value: T): T =
    if (T::class.isInline) inlineValue(value)
    else value

@Suppress("UNCHECKED_CAST")
fun <T : Any> inlineValue(value: T): T {
   return value::class.java.declaredMethods
        .find { it.name == "unbox-impl" }!!
        .invoke(value) as T
}

inline fun <reified T : Any> MockKMatcherScope.anyValue(): T =
    if (T::class.isInline) anyInlineValue()
    else any()

inline fun <reified T : Any> MockKMatcherScope.anyInlineValue(): T =
    T::class.primaryConstructor!!.run {
        val valueType = parameters[0].type.classifier as KClass<*>
        val any = match(ConstantMatcher<Any>(true), valueType)
        call(any)
    }

fun <T : Any> MockKMatcherScope.match(matcher: Matcher<T>, type: KClass<T>): T =
    (getProperty("callRecorder") as MockKGateway.CallRecorder).matcher(matcher, type)

val KClass<*>.isInline: Boolean
    get() = !isData &&
        primaryConstructor?.parameters?.size == 1 &&
        java.declaredMethods.any { it.name == "box-impl" }
