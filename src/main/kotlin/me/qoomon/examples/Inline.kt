package me.qoomon.examples

@JvmInline
value class DummyString constructor(val value: String) {

    init {
        require(value.length <= 32) { "length is greater than 32" }
    }

    constructor(value: Int) : this(value.toString())
}

fun String.asDummyString() = DummyString(this)

fun main() {
    DummyString("John")
    DummyString(42)
    "John".asDummyString()
}
