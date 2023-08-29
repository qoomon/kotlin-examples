@file:Suppress("MagicNumber")
package me.qoomon.examples

import kotlin.random.Random

private val DEFAULT_CHAR_SET: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
fun randomString(
    length: Int,
    charSet: List<Char> = DEFAULT_CHAR_SET,
    random: Random = Random.Default,
) =
    random.nextBytes(length)
        .map { it.toInt() and 0xFF } // transform to unsigned int between 0 and 255
        .map { charSet.elementAt(it % charSet.size) }
        .joinToString("")

fun main() {
    val random = Random.Default
    repeat(1000) {
        println(randomString(16, random = random))
    }
}
