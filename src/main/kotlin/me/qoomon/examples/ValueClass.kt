package me.qoomon.examples

import me.qoomon.enhancements.kotlin.Requirements.Companion.requirements

@JvmInline
value class DummyString constructor(val value: String) {
    init {
        requirements {
            require(value.length <= 8) { "value length should be less than 8" }
            require(value == value.lowercase()) { "value should be lowercase" }
        }
    }
}

fun main() {
    DummyString("Steve--------")
}
