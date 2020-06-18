package me.qoomon.examples

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.MDC
import kotlin.random.Random

private val LOG = logger {}

fun main() {
    MDC.put("ContextInfo", "Hello!")
    LOG.info("START")
    runBlocking(IO + MDCContext()) {
        (0 until 100).forEach {
            launch {

                LOG.info("$it started")
                delay(100)
                if (Random.nextDouble() < 0.1) throw Exception("$it throws exception")
                LOG.info("$it finished")
            }
        }
    }
    LOG.info("END")
}
