package me.qoomon.examples

import kotlin.reflect.KProperty

fun <T> mementoFactory(initValue: T, method: T.() -> T): MementoFactory<T> = MementoFactory(initValue, method)
class MementoFactory<T>(initValue: T, private val method: T.() -> T) {

    private var value: T = initValue

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        value.method().also { this.value = it }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name} in $thisRef.'")
    }
}

private var foo: String by MementoFactory("moin") { "$this!" }

class Dummy2 {
    var bar: String by Delegate()
}

fun main() {
    println(foo)
    foo = "hallo"
    println(foo)
    println(foo)

    val dummy = Dummy2()
    println(dummy.bar)
    dummy.bar = "abc"
    println(dummy.bar)
}
