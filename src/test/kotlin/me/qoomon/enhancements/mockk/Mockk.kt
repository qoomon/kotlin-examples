package me.qoomon.enhancements.mockk

import io.mockk.verify
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

fun <T : Any> verifyAllMemberPropertiesGet(obj: T, vararg except: KProperty1<T, *>) {
    verify {
        obj::class.memberProperties.forEach {
            if (it in except) return@forEach
            obj getProperty it.name
        }
    }
}

