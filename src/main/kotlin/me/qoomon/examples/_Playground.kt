package me.qoomon.examples

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun main() = runBlocking<Unit> {
    (1..10).forEach {
            launch {
            }
    }
}
