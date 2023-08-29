package me.qoomon.examples

@Suppress("UseCheckOrError")
fun main() {
    val resultA: Result<String> = runCatching { "Hello, World" }
    val resultB: Result<String> = runCatching { throw IllegalStateException("Boom!") }

    resultA.onSuccess { result ->
        println(result)
    }
    resultB.onFailure { exception ->
        println(exception)
    }


    println(
        try {
            throw IllegalStateException("Boom!")
        } catch (
            @Suppress("SwallowedException")
            exception: IllegalStateException
        ) {

            "Fallback"
        },
    )
}
