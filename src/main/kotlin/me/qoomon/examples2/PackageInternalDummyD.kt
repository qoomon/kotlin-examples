package me.qoomon.examples2

import me.qoomon.PackageInternal
import me.qoomon.PackageInternalDummyA

@PackageInternal
internal open class PackageInternalDummyD @PackageInternal internal constructor() : PackageInternalDummyA() {
    @PackageInternal
    internal val foo: String = "" // TODO

    init {
        PackageInternalDummyA().valueXXX
    }
}
