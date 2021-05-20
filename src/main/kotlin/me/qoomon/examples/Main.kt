package me.qoomon.examples

@JvmInline
value class Password(
    val value: String
)

fun main() {
    println("Hello, world!!! " + String::class.isValue)
}
