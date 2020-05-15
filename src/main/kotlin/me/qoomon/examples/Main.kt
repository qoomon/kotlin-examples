package me.qoomon.examples

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.time.Duration
import kotlin.time.measureTime


fun main(args: Array<String>) {


    println("foo".hash())
}

fun String.hash(algorithm: String = "sha-256"): String =
    MessageDigest.getInstance(algorithm).let {
        BigInteger(1, it.digest(toByteArray())).toString(16)
            .padStart(32, '0')
    }
