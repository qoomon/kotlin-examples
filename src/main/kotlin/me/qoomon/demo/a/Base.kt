package me.qoomon.demo.a

import me.qoomon.enhancements.kotlin.PackageInternal
@PackageInternal
internal open class Base {

    @PackageInternal
    internal open val isBuzz: Boolean = true
    internal open val isMoo: String = "true"
    internal open val fizz: Boolean = true

    @PackageInternal
    internal open fun moin() {
    }
}
