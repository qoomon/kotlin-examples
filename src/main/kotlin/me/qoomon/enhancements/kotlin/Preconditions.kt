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

class ConditionsContext constructor(val lazyContextMessage: () -> Any) {

    fun require(value: Boolean, lazyMessage: () -> Any) {
        contract {
            returns() implies value
        }
        kotlin.require(value) { lazyMessage().toString().appendContext() }
    }

    fun check(value: Boolean, lazyMessage: () -> Any) {
        contract {
            returns() implies value
        }
        kotlin.check(value) { lazyMessage().toString().appendContext() }
    }

    fun error(message: Any): Nothing = kotlin.error(message.toString().appendContext())

    private fun String.appendContext() = this + "\n" +
        "\t\t" + lazyContextMessage()
}

fun preconditionsContext(contextMessage: Any, block: ConditionsContext.() -> Unit) {
    preconditionsContext({ contextMessage }, block)
}

fun preconditionsContext(lazyContextMessage: () -> Any, block: ConditionsContext.() -> Unit) {
    ConditionsContext(lazyContextMessage).apply { block() }
}
