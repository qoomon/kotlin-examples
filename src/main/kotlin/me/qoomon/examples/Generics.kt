package me.qoomon.examples

@Suppress("UNCHECKED_CAST")
private inline fun <SELF> Any.self(block: () -> Unit): SELF {
    block()
    return this as SELF
}

open class A<SELF : A<SELF>> {
    fun actionA(): SELF = self { println("actionA") }
}

open class B<SELF : B<SELF>> : A<SELF>() {
    fun actionB(): SELF = self { println("actionB") }
}

fun main() {
    val a: A<*> = A().actionA()
    val b: B<*> = B().actionA().actionB()
    println(a.toString())
    println(b.toString())
}
