package me.qoomon.enhancements.kotlin

import java.nio.charset.Charset
import java.util.*

fun String.decodeBase64(charset: Charset = Charsets.UTF_8): String {
    return Base64.getDecoder().decode(this).toString(charset)
}

fun String.encodeBase64(charset: Charset = Charsets.UTF_8): String {
    return Base64.getEncoder().encodeToString(this.toByteArray(charset))
}
