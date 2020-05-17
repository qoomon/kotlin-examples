package me.qoomon.examples


inline infix fun (() -> Boolean).and(crossinline that: () -> Boolean): () -> Boolean = { this() && that() }

inline infix fun (() -> Boolean).or(crossinline that: () -> Boolean): () -> Boolean = { this() || that() }

inline infix fun (() -> Boolean).xor(crossinline that: () -> Boolean): () -> Boolean = { this() xor that() }
