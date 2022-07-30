package me.qoomon.enhancements.mockk

import io.mockk.verify
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

fun <T : Any> verifyAllPropertyGetters(obj: T, vararg except: KProperty1<T, *>) {
    verify {
        obj::class.memberProperties.forEach {
            if (it in except) return@forEach
            obj getProperty it.name
        }
    }
}

fun main() {
    for (property in Result.success(1)::class.java.declaredFields) {
        println("${property.name}")
    }

    Result::class.memberProperties.filter { it.javaField != null }
    Result::class.declaredMemberProperties.filter { it.javaField != null }
}
