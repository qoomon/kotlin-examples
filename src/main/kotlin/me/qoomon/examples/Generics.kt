package me.qoomon.examples

@Suppress("UNCHECKED_CAST")
fun <SELF> Any.fluent(block: () -> Unit): SELF {
    block()
    return this as SELF
}

open class A<SELF : A<SELF>> {
    fun actionA(): SELF = fluent { println("actionA") }
}

open class B<SELF : B<SELF>> : A<SELF>() {
    fun actionB(): SELF = fluent { println("actionB") }
}

fun main() {
    val a: A<*> = A().actionA()
    val b: B<*> = B().actionA().actionB()
    println(a.toString())
    println(b.toString())
}
