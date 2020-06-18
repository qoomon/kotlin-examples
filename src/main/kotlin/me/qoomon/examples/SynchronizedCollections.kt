package me.qoomon.examples

import java.util.*

class SynchronizedMutableCollection<T>(collection: MutableCollection<T> = mutableListOf()) :
    MutableCollection<T> by Collections.synchronizedCollection(collection)
