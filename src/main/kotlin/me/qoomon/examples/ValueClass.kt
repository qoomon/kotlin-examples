package me.qoomon.examples

@JvmInline
value class DummyString constructor(val value: String) {
    init {
        require(
            { require(value.length <= 8) { "length should be less than 8, but was $value" } },
            { require(value == value.lowercase()) { "should be lowercase, but was $value" } },
        )
    }
}

fun require(vararg requirements: () -> Unit) {
    val exceptions = requirements.mapNotNull {
        try {
            it(); null
        } catch (ex: IllegalArgumentException) {
            ex
        }
    }
    if (exceptions.isNotEmpty()) {
        throw IllegalArgumentException("Failed requirements.").apply {
            val self = stackTrace[0]
            // hide require call from stacktrace
            stackTrace = stackTrace.drop(1).toTypedArray()
            exceptions.forEach { ex ->
                ex.apply {
                    stackTrace = stackTrace.filterNot {
                        it.className == self.className &&
                            it.methodName == self.methodName
                    }.toTypedArray()
                }
                addSuppressed(ex)
            }
        }
    }
}

fun main() {
    DummyString("Steve--------")
}
