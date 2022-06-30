package me.qoomon.examples

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.qoomon.enhancements.kotlin.preconditionsContext

@JvmInline
value class DummyString constructor(val value: String) {
    init {
        preconditionsContext(this) {
            require(value.length <= 8) { "value length should be less than 8" }
            require(value == value.lowercase()) { "value should be lowercase" }
        }
    }
}

fun main() {
    DummyString("Steve--------")
}
