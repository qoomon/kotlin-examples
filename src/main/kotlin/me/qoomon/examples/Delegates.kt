package me.qoomon.examples

import kotlin.reflect.KProperty

fun <T> mementoFactory(method: T?.() -> T): MementoFactory<T> = MementoFactory(method)
class MementoFactory<T>(private val method: T?.() -> T) {

    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        value.method().also { this.value = it }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}


