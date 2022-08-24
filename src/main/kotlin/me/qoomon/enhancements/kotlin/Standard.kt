package me.qoomon.enhancements.kotlin

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T = apply { if (condition) block() }
