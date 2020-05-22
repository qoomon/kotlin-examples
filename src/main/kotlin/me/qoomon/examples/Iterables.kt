package me.qoomon.examples

fun <T> combine(vararg iterators: Iterator<T>) = iterator {
    iterators.forEach { yieldAll(it) }
}

fun <T> Iterator<T>.toIterable() = Iterable { this }
