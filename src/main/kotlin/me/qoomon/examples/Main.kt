package me.qoomon.examples

fun main(args: Array<String>) {
    println("main")
    args.forEach { println("\t$it)") }
    val input = readLine()
    println("input: $input")
}
