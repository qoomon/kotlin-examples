package me.qoomon.enhancements.kotlin

inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> Unit): T = apply {
    if (predicate(this)) block(this)
}

inline fun <T> T.letIf(predicate: T.() -> Boolean, block: T.() -> T): T = let {
    if (predicate(this)) block(this) else it
}
