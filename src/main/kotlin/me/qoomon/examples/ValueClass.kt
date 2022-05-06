package me.qoomon.examples

@JvmInline
value class DummyString constructor(val value: String) {
    init {
        require(value.length <= 8) { "length should be less than 8, but was $value" }
        require(value == value.lowercase()) { "should be lowercase, but was $value" }
    }
}

fun main() {
    DummyString("Steve--------")
}
