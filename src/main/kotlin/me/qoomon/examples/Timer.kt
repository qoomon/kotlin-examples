package me.qoomon.examples

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

fun schedule(
    period: Duration,
    delay: Duration = Duration.ZERO,
    @OptIn(DelicateCoroutinesApi::class)
    scope: CoroutineScope = GlobalScope,
    block: () -> Unit,
) = scope.launch {
    delay(delay)
    while (isActive) {
        block()
        delay(period)
    }
}
