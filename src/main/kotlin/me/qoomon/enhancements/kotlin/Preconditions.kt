package me.qoomon.enhancements.kotlin

import kotlin.contracts.contract
import kotlin.reflect.KClass

class Requirements private constructor() {
    private val exceptions = mutableListOf<IllegalArgumentException>()

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

class Checks private constructor() {
    private val exceptions = mutableListOf<IllegalStateException>()

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

private fun <T : Throwable> T.removeSelfStackTraceElements(self: KClass<*>): T {
    val qualifiedName = self.qualifiedName!!
    val qualifiedNameSubClassPrefix = "$qualifiedName$"
    stackTrace = stackTrace.filterNot {
        it.className == qualifiedName || it.className.startsWith(qualifiedNameSubClassPrefix)
    }.toTypedArray()
    suppressed.forEach { it.removeSelfStackTraceElements(self) }
    return this
}
