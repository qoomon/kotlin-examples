package me.qoomon.examples

import org.slf4j.MDC

fun <T> MDC(vararg pairs: Pair<String, String>, block: () -> T): T {
    pairs.forEach { MDC.put(it.first, it.second) }
    try {
        return block()
    } finally {
        pairs.forEach { MDC.remove(it.first) }
    }
}
