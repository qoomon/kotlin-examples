package me.qoomon.demo.a

import me.qoomon.enhancements.kotlin.PackageInternal

internal class Foo : Base(), BaseInterface {

    override val isBuzz: Boolean = false

    override fun moin() {
    }

    @PackageInternal
    internal fun hello() {
    }


}
