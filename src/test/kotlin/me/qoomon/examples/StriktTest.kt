package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class StriktTest {

    @Test
    fun `expectThat satisfy`() {
        expectThat("foo") {
            pass { assert(it == "foox") { "name is wrong" } }
        }
    }

    @Test
    fun `expect that satisfy`() {
        expect {
            that("foo") {
                pass { assert(it == "foo") { "name is wrong" } }
            }
        }
    }
}
