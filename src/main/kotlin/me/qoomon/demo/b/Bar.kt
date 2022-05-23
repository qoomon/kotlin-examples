package me.qoomon.demo.b

import Bengt
import me.qoomon.demo.a.Foo
import kotlin.reflect.KFunction0

class Bar {
    private val boom: Foo? = null

    init {
        // val foo = Foo()
        // foo?.moin()
        // foo?.buzz
        // foo?.hello()
        // ref(foo::hello)

        Bengt()
    }

    fun ref(block: KFunction0<Unit>) {}
}
