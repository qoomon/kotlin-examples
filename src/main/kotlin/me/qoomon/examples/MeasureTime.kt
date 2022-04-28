package me.qoomon.examples

import kotlin.time.Duration
import kotlin.time.measureTime

inline fun <T> measureTime(callback: (Duration) -> Unit, block: () -> T): T {
    var result: T
    measureTime { result = block() }.also { callback(it) }
    return result
}

fun main() {
    measureTime({
        println(it)
    }) {
        Math.random()
    }
}
