package me.qoomon.examples

import java.time.Instant
import java.time.temporal.Temporal
import kotlin.time.Duration
import kotlin.time.toJavaDuration


@Suppress("UNCHECKED_CAST")
fun <T : Temporal> Duration.ago(from: T): T = (from - this.toJavaDuration()) as T

fun Duration.ago() = ago(Instant.now())

fun <T> T.isWithinLast(duration: Duration, from: T): Boolean where T : Temporal, T : Comparable<T> =
    duration.ago(from).let { this >= it && this < from }

fun Instant.isWithinLast(duration: Duration) = isWithinLast(duration, Instant.now())


@Suppress("UNCHECKED_CAST")
fun <T : Temporal> Duration.ahead(from: T): T = (from + this.toJavaDuration()) as T

fun Duration.ahead() = ahead(Instant.now())

fun <T> T.isWithinNext(duration: Duration, from: T): Boolean where T : Temporal, T : Comparable<T> =
    duration.ahead(from).let { this <= it && this > from }

fun Instant.isWithinNext(duration: Duration) = isWithinNext(duration, Instant.now())
