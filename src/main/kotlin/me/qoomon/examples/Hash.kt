package me.qoomon.examples

import java.math.BigInteger
import java.security.MessageDigest

fun String.hash(algorithm: String = "SHA-256"): String {
    val hashBytes = MessageDigest.getInstance(algorithm).digest(toByteArray())
    return BigInteger(1, hashBytes).toString(16)
        .padStart(hashBytes.size * 2, '0')
}
