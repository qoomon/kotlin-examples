package me.qoomon.examples

import VisibilityPackage

fun main() {

    println((listOf(1, 2, 3) == listOf(1, 2, 3)))
}

class VisibilityPackageDummyA constructor() {

    @VisibilityPackage
    val value = "A"
        get

    companion object {
        val FOO = "MOIN"
            get
    }
}

class VisibilityPackageDummyB @VisibilityPackage constructor() {
    @VisibilityPackage
    var value = VisibilityPackageDummyA().value
        @VisibilityPackage get

    init {
        VisibilityPackageDummyA.FOO
        value = "abs"
    }
}
