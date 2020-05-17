package me.qoomon.examples

import java.math.BigInteger
import java.security.MessageDigest

fun String.hash(algorithm: String = "SHA-256"): String =
    MessageDigest.getInstance(algorithm)
        .digest(toByteArray()).let { bytes ->
            BigInteger(1, bytes)
                .toString(16)
                .padStart(bytes.size * 2, '0')
        }
