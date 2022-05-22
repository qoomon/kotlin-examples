package me.qoomon.demo.a

import me.qoomon.enhancements.kotlin.PackageInternal
@PackageInternal
internal open class Base {

    @PackageInternal
    internal open val buzz: String = "testBase"

    @PackageInternal
    internal open fun moin() {
    }
}
