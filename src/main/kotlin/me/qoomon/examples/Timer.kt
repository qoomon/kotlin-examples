package me.qoomon.examples

import kotlinx.coroutines.*
import kotlin.time.Duration

fun schedule(
    period: Duration,
    delay: Duration = Duration.ZERO,
    @OptIn(DelicateCoroutinesApi::class)
    scope: CoroutineScope = GlobalScope,
    block: () -> Unit
) = scope.launch {
    delay(delay)
    while (isActive) {
        block()
        delay(period)
    }
}

