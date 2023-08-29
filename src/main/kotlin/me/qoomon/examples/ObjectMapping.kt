package me.qoomon.examples

import kotlin.time.measureTime

data class Dummy(val foo: String, val bar: Int)
data class DummyDTO(val foo: String)

@Suppress("MagicNumber")
fun main() {
    val times = 10_00_000

    repeat(10) {
        measureTime {
            repeat(times) {
                Dummy(foo = "moin", bar = 42).toDTO()
            }
        }.also { println("toDTO1: $it") }
    }
    println()
}

fun Dummy.toDTO() = DummyDTO(
    foo = foo,
)
