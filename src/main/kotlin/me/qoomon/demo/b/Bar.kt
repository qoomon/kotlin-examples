package me.qoomon.demo.b

import me.qoomon.demo.a.Foo

class Bar {
    private var foo: Foo? = null

    init {
        foo = Foo()
        foo?.moin()
        foo?.buzz
        foo?.hello()
    }
}
