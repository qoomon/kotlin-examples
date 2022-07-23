package me.qoomon.enhancements.kotlin

inline infix fun (() -> Boolean).and(crossinline that: () -> Boolean): () -> Boolean = { this() && that() }

inline infix fun (() -> Boolean).or(crossinline that: () -> Boolean): () -> Boolean = { this() || that() }

inline infix fun (() -> Boolean).xor(crossinline that: () -> Boolean): () -> Boolean = { this() xor that() }

fun <T> T.satisfyAny(vararg conditions: (T) -> Boolean) = conditions.any { it(this) }
fun <T> T.satisfyAll(vararg conditions: (T) -> Boolean) = conditions.all { it(this) }

