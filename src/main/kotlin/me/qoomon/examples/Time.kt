package me.qoomon.examples

import java.time.Instant
import java.time.temporal.Temporal
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Suppress("UNCHECKED_CAST")
fun <T : Temporal> Duration.ago(from: T): T = (from - this.toJavaDuration()) as T

fun Duration.ago() = ago(Instant.now())

fun <T> T.isWithinLast(
    duration: Duration,
    from: T,
    includeFuture: Boolean = true
): Boolean where T : Temporal, T : Comparable<T> =
    duration.ago(from).let { past -> past <= this && (includeFuture || this < from) }

fun Instant.isWithinLast(duration: Duration, includeFuture: Boolean = true) =
    isWithinLast(duration, Instant.now(), includeFuture)

@Suppress("UNCHECKED_CAST")
fun <T : Temporal> Duration.ahead(from: T): T = (from + this.toJavaDuration()) as T

fun Duration.ahead() = ahead(Instant.now())

fun <T> T.isWithinNext(
    duration: Duration,
    from: T,
    includePast: Boolean = true
): Boolean where T : Temporal, T : Comparable<T> =
    duration.ahead(from).let { future -> (includePast || from < this) && this <= future }

fun Instant.isWithinNext(duration: Duration, includePast: Boolean = true) =
    isWithinNext(duration, Instant.now(), includePast)
