package me.qoomon.enhancements.mockk

import io.mockk.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

fun <T : Any> verifyAllMemberPropertiesGet(obj: T, vararg except: KProperty1<T, *>) {
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

    val callRecorder = getProperty("callRecorder") as MockKGateway.CallRecorder

    val primaryValueConstructor = T::class.primaryConstructor!!.apply { isAccessible = true }
    val valueType = primaryValueConstructor.let { it.parameters[0].type.classifier as KClass<*> }

    val anyValue = callRecorder.matcher(ConstantMatcher<T>(true), valueType)
    return primaryValueConstructor.call(anyValue)
}

// doesn't work :(

inline fun <reified T : Any> MockKMatcherScope.captureValue(slot: CapturingSlot<T>): T {
    if (!T::class.isValue) return capture(slot)

    val callRecorder = getProperty("callRecorder") as MockKGateway.CallRecorder

    val primaryValueConstructor = T::class.primaryConstructor!!.apply { isAccessible = true }
    val valueType = primaryValueConstructor.let { it.parameters[0].type.classifier as KClass<*> }

    val anyValue = callRecorder.matcher(CapturingValueSlotMatcher(slot, valueType, primaryValueConstructor), valueType)
    return primaryValueConstructor.call(anyValue)
}

data class CapturingValueSlotMatcher<T : Any>(
    val captureSlot: CapturingSlot<T>,
    override val argumentType: KClass<*>,
    val valueConstructor: KFunction<T>
) : Matcher<T>, CapturingMatcher, TypedMatcher, EquivalentMatcher {
    override fun equivalent(): Matcher<Any> = ConstantMatcher<Any>(true)

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
