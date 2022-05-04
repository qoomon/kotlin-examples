package me.qoomon.examples

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.MDC
import kotlin.random.Random

private val log: KLogger = KotlinLogging.logger {}

fun main() {
    MDC.put("ContextInfo", "Hello!")
    log.info("START")
    runBlocking(IO + MDCContext()) {
        (0 until 100).forEach {
            launch {
                log.info("$it started")
                delay(100)
                if (Random.nextDouble() < 0.1) throw Exception("$it throws exception")
                log.info("$it finished")
            }
        }
    }
    log.info("END")
}
