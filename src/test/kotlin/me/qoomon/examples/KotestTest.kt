package me.qoomon.examples

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
class KotestTest {
    @Test
    fun test() {
        val foo = "foo"

        foo shouldBe "foo"
        foo shouldContain  "oo"

        assertSoftly {
            foo shouldBe "foo"
            foo shouldContain  "oo"
        }
    }
}
