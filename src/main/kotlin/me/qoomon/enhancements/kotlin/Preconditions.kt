package me.qoomon.enhancements.kotlin

import me.qoomon.enhancements.kotlin.Requirements.Companion.requirements
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.time.measureTime

// --- Usage Example ---------------------------------------------------------------------------------------------------

@JvmInline
value class DummyString1 constructor(val value: String) {
    init {
        require(value.length <= 8) { "value length should be less than 8" }
        require(value == value.lowercase()) { "value should be lowercase" }
    }
}

@JvmInline
value class DummyString2 constructor(val value: String) {
    init {
        requirements {
            require(value.length <= 8) { "value length should be less than 8" }
            require(value == value.lowercase()) { "value should be lowercase" }
        }
    }
}

fun main() {
    println(
        measureTime {
            repeat(1_000_000_000) {
                DummyString2("abc")
            }
        }
    )
}

// --- Implementation ---------------------------------------------------------------------------------------------------

@JvmInline
value class Requirements private constructor(
    private val exceptions: MutableList<IllegalArgumentException> = mutableListOf()
) {

    fun require(value: Boolean, lazyMessage: () -> Any = { "Failed requirement." }) {
        contract {
            returns() implies value
        }
        if (!value) {
            val message = lazyMessage()
            exceptions += IllegalArgumentException(message.toString())
        }
    }

    companion object {
        fun requirements(block: Requirements.() -> Unit) = requirements(null) { block() }

        fun <T> requirements(subject: T?, block: Requirements.(T?) -> Unit) {
            Requirements().apply { block(subject) }.exceptions.run {
                if (isEmpty()) return

                throw IllegalArgumentException(
                    "${subject ?: ""}" +
                    "\n\tFailed requirement(s):" +
                    "\n${joinToString("\n") { "\t\t- " + it.message }}"
                )
                    .apply { forEach(::addSuppressed) }
                    .apply { removeSelfStackTraceElements(Requirements::class) }
            }
        }
    }
}
@JvmInline
value class Checks private constructor(
    private val exceptions: MutableList<IllegalStateException> = mutableListOf()
) {

    fun check(value: Boolean, lazyMessage: () -> Any = { "Check failed." }) {
        contract {
            returns() implies value
        }
        if (!value) {
            val message = lazyMessage()
            exceptions += IllegalStateException(message.toString())
        }
    }

    companion object {
        fun checks(block: Checks.() -> Unit) = checks(null) { block() }

        fun <T> checks(subject: T?, block: Checks.(T?) -> Unit) {
            Checks().apply { block(subject) }.exceptions.run {
                if (isEmpty()) return

                throw IllegalStateException(
                    "${subject ?: ""}" +
                    "\n\tCheck(s) failed:" +
                    "\n${joinToString("\n") { "\t\t- " + it.message }}"
                )
                    .apply { forEach(::addSuppressed) }
                    .apply { removeSelfStackTraceElements(Checks::class) }
            }
        }
    }
}

fun Throwable.removeSelfStackTraceElements(self: KClass<*>) {
    val qualifiedName = self.qualifiedName!!
    val qualifiedNameSubClassPrefix = "$qualifiedName$"
    stackTrace = stackTrace.filterNot {
        it.className == qualifiedName || it.className.startsWith(qualifiedNameSubClassPrefix)
    }.toTypedArray()
    suppressed.forEach { it.removeSelfStackTraceElements(self) }
}
