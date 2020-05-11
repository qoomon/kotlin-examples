package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.message

class StriktUtilsTest {

    @Test
    fun satisfy() {
        // Given
        val assertion = { assert(false) { "Boom!" } }

        // When
        val result = runCatching {
            expect {
                that("foo").pass { assertion() }
            }
        }

//        // Then
//        expectThat(result).isFailure().and {
//            isA<AssertionFailedError>()
//            cause.isA<AssertionError>()
//            cause.get { this?.message }.isEqualTo("Boom!")
//        }
    }
}
