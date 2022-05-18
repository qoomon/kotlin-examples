package me.qoomon.examples


fun main() {

    println((listOf(1, 2, 3) == listOf(1, 2, 3)))

}

open class PackageInternalDummyA internal constructor() {

    @PackageInternal
    internal val valueXXX: String? = "A"

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
        value = "abs"
        Foo()
    }

    // @PackageInternal
    class Foo()
}

