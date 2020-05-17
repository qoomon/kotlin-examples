package me.qoomon.examples

import java.time.LocalDateTime
import java.time.temporal.Temporal
import kotlin.time.Duration
import kotlin.time.toJavaDuration


@Suppress("UNCHECKED_CAST")
fun <T : Temporal> Duration.ago(from: T): T = (from - this.toJavaDuration()) as T

fun Duration.ago() = ago(LocalDateTime.now())

fun <T> T.withinLast(duration: Duration, from: T): Boolean where T : Temporal, T : Comparable<T> {
    val limit = duration.ago(from)
    return (this > limit || this == limit) && this < from
}

fun LocalDateTime.withinLast(duration: Duration) = withinLast(duration, LocalDateTime.now())


@Suppress("UNCHECKED_CAST")
fun <T : Temporal> Duration.ahead(from: T): T = (from + this.toJavaDuration()) as T

fun Duration.ahead() = ahead(LocalDateTime.now())

fun <T> T.withinNext(duration: Duration, from: T): Boolean where T : Temporal, T : Comparable<T> {
    val limit = duration.ahead(from)
    return (this < limit || this == limit) && this > from
}

fun LocalDateTime.withinNext(duration: Duration) = withinNext(duration, LocalDateTime.now())
