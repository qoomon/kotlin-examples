package me.qoomon.examples


fun main() {
    val resultA: Result<String> = runCatching { "Hello, World" }
    val resultB: Result<String> = runCatching { throw Exception("Boom!") }

    resultA.onSuccess { result ->
        println(result)
    }
    resultB.onFailure { exception ->
        println(exception)
    }


    println(
        try {
            throw Exception("Boom!")
        } catch (exception: Exception) {
            "Fallback"
        }
    )
}
