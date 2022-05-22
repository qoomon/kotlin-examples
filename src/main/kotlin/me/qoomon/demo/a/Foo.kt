package me.qoomon.demo.a

import me.qoomon.enhancements.kotlin.PackageInternal

internal class Foo : Base(), BaseInterface {

    override val buzz: String = "testFoo"

    override fun moin() {
    }

    @PackageInternal
    internal fun hello() {
    }


}
