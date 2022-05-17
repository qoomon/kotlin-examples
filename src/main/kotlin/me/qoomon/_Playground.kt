package me.qoomon

import me.qoomon.examples2.PackageInternalDummyD

fun main() {

    println((listOf(1, 2, 3) == listOf(1, 2, 3)))
}

open class PackageInternalDummyA internal constructor() {

    @PackageInternal
    internal val valueXXX = "A"

    companion object {
        val FOO = "MOIN"
            get
    }
}

class PackageInternalDummyB @PackageInternal internal constructor() {
    @PackageInternal
    var value = PackageInternalDummyA().valueXXX
        private set

    init {
        PackageInternalDummyD()
        value = "abs"
        Foo()
    }

    // @PackageInternal
    class Foo()
}

 internal open class A() {
     public val foo = ""
 }

