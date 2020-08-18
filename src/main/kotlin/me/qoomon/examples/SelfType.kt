package me.qoomon.examples

open class Parent<T : Parent<T>> {

    @Suppress("UNCHECKED_CAST")
    inline fun self(block: T.() -> Unit = {}): T = (this as T).apply { block() }

    fun hello(): T = self {
        println("world")
    }
}

open class Child : Parent<Child>() {

    fun ping() = self {
        println("pong")
    }
}

fun main() {
    Child().hello().ping()
}
