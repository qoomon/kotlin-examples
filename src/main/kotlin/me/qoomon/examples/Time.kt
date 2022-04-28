package me.qoomon.examples

import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.Instant
import kotlin.time.Duration

fun Duration.ago(from: Instant): Instant = from - this

fun Duration.ago() = ago(now())

fun Instant.isWithinLast(
    duration: Duration,
    from: Instant,
    includeFuture: Boolean = true
): Boolean = duration.ago(from)
    .let { past -> past <= this && (includeFuture || this < from) }

fun Instant.isWithinLast(duration: Duration, includeFuture: Boolean = true) =
    isWithinLast(duration, now(), includeFuture)

fun Duration.ahead(from: Instant) = from + this

fun Duration.ahead() = ahead(now())

fun Instant.isWithinNext(
    duration: Duration,
    from: Instant,
    includePast: Boolean = true
): Boolean = duration.ahead(from)
    .let { future -> (includePast || from < this) && this <= future }

fun Instant.isWithinNext(duration: Duration, includePast: Boolean = true) =
    isWithinNext(duration, now(), includePast)
