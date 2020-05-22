package me.qoomon.examples


fun main() {
    val regex = "(?<!\\*\\*)$".toRegex()
    println("foo/bar".replace(regex, ".class"))
    println("foo/**".replace(regex, ".class"))
}

