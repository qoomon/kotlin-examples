package me.qoomon.enhancements.mockk

import io.mockk.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

fun <T : Any> verifyGetForAllMemberProperties(obj: T, vararg except: KProperty1<T, *>) {
    verify {
        obj::class.memberProperties.forEach {
            if (it in except) return@forEach
            obj getProperty it.name
        }
    }
}

// Work-around for mocking inline-classes
// https://github.com/mockk/mockk/issues/152#issuecomment-631796323

inline fun <reified T : Any> MockKMatcherScope.anyValue(): T {
    if (!T::class.isValue) return any()

    val constructor = T::class.primaryConstructor!!.apply { isAccessible = true }
    val rawType = constructor.parameters[0].type.classifier as KClass<*>

    val anyRawValue = callRecorder.matcher(ConstantMatcher<T>(true), rawType)
    return constructor.call(anyRawValue)
}

inline fun <reified T : Any> MockKMatcherScope.captureValue(slot: CapturingSlot<T>): T {
    if (!T::class.isValue) return capture(slot)

    val constructor = T::class.primaryConstructor!!.apply { isAccessible = true }
    val rawType = constructor.parameters[0].type.classifier as KClass<*>

    val anyRawValue = callRecorder.matcher(CapturingValueSlotMatcher(slot, constructor, rawType), rawType)
    return constructor.call(anyRawValue)
}

val MockKMatcherScope.callRecorder: MockKGateway.CallRecorder
    get() = getProperty("callRecorder") as MockKGateway.CallRecorder

data class CapturingValueSlotMatcher<T : Any>(
    val captureSlot: CapturingSlot<T>,
    val valueConstructor: KFunction<T>,
    override val argumentType: KClass<*>,
) : Matcher<T>, CapturingMatcher, TypedMatcher, EquivalentMatcher {
    override fun equivalent(): Matcher<Any> = ConstantMatcher(true)

    @Suppress("UNCHECKED_CAST")
    override fun capture(arg: Any?) {
        if (arg == null) {
            captureSlot.isNull = true
        } else {
            captureSlot.isNull = false
            captureSlot.captured = valueConstructor.call(arg)
        }
        captureSlot.isCaptured = true
    }

    override fun match(arg: T?): Boolean = true

    override fun toString(): String = "slotCapture<${argumentType.simpleName}>()"
}
