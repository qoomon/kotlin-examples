package me.qoomon.enhancements.kotlin

import kotlin.contracts.contract

/**
 * Usage Example
 * ```kotlin
 *  @JvmInline
 *  value class Dummy constructor(val value: String) {
 *      init {
 *          preconditionsContext(this) {
 *              require(value.length <= 8) { "value length should be less than 8" }
 *              require(value == value.lowercase()) { "value should be lowercase" }
 *          }
 *      }
 *  }
 * ```
 */


class Preconditions constructor(val message: () -> Any) {

    fun require(value: Boolean, message: () -> Any) {
        contract {
            returns() implies value
        }
        kotlin.require(value) { message().toString() + "\n" + this.message() }
    }

    fun error(message: Any): Nothing = kotlin.error(message.toString() + "\n" + this.message())
}

class Postconditions constructor(val message: () -> Any) {

    fun check(value: Boolean, message: () -> Any) {
        contract {
            returns() implies value
        }
        kotlin.check(value) { message().toString() + "\n" + this.message() }
    }

    fun error(message: Any): Nothing = kotlin.error(message.toString() + "\n" + this.message())
}

fun preconditionsContext(message: Any, block: Preconditions.() -> Unit) {
    preconditionsContext({ message }, block)
}

fun preconditionsContext(message: () -> Any, block: Preconditions.() -> Unit) {
    Preconditions(message).apply { block() }
}

fun postconditionsContext(message: Any, block: Postconditions.() -> Unit) {
    postconditionsContext({ message }, block)
}

fun postconditionsContext(message: () -> Any, block: Postconditions.() -> Unit) {
    Postconditions(message).apply { block() }
}

