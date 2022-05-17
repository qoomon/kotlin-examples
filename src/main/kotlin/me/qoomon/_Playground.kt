package me.qoomon

import me.qoomon.examples2.PackageInternalDummyD

fun main() {

    println((listOf(1, 2, 3) == listOf(1, 2, 3)))
}

open class PackageInternalDummyA @PackageInternal internal constructor() {

    @PackageInternal
    internal val value = "A"

    companion object {
        val FOO = "MOIN"
            get
    }
}

class PackageInternalDummyB @PackageInternal internal constructor() {
    @PackageInternal
    internal var value = PackageInternalDummyA().value
        get

    init {
        PackageInternalDummyD()
        value = "abs"
        Foo()
    }

    // @PackageInternal
    class Foo()
}
