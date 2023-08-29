@file:Suppress("MatchingDeclarationName")
package me.qoomon.examples

import me.qoomon.enhancements.kotlin.preconditionsContext

@JvmInline
value class DummyString constructor(val value: String) {
    init {
        preconditionsContext(this) {
            @Suppress("MagicNumber")
            require(value.length <= 8) { "value length should be less than 8" }
            require(value == value.lowercase()) { "value should be lowercase" }
        }
    }
}

fun main() {
    DummyString("Steve--------")
}
