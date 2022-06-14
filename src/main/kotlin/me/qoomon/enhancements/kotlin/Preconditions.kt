package me.qoomon.enhancements.kotlin

import me.qoomon.enhancements.kotlin.ConditionsContext.Companion.conditionsContext
import kotlin.contracts.contract
import kotlin.time.measureTime

// --- Usage Example ---------------------------------------------------------------------------------------------------

@JvmInline
value class Dummy constructor(val value: String) {
    init {
        conditionsContext(this) {
            require(value.length <= 8) { "value length should be less than 8" }
            require(value == value.lowercase()) { "value should be lowercase" }
        }
    }
}

fun main() {
    // Dummy("abcA")
    repeat(10) {
        println(
            measureTime {
                repeat(1_000_000_000) {
                    Dummy("abc")
                }
            }
        )
    }
}

// --- Implementation ---------------------------------------------------------------------------------------------------

class ConditionsContext(private val lazyContextMessage: () -> Any) {

    inline fun require(value: Boolean, lazyMessage: () -> Any = { "Failed requirement." }) {
        contract {
            returns() implies value
        }
        kotlin.require(value) { lazyMessage().toString().addContext() }
    }

    inline fun check(value: Boolean, lazyMessage: () -> Any = { "Check failed." }) {
        contract {
            returns() implies value
        }
        kotlin.check(value) { lazyMessage().toString().addContext() }
    }

    fun error(message: Any): Nothing = kotlin.error(message.toString().addContext())

    fun String.addContext() = this + "\n" +
                              "\t\t" + lazyContextMessage().toString()

    companion object {

        inline fun conditionsContext(contextMessage: Any, block: ConditionsContext.() -> Unit) {
            conditionsContext({ contextMessage }, block)
        }

        inline fun conditionsContext(noinline lazyContextMessage: () -> Any, block: ConditionsContext.() -> Unit) {
            ConditionsContext(lazyContextMessage).apply { block() }
        }
    }
}
