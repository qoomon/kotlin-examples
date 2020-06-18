package me.qoomon.examples

import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import strikt.api.CompoundAssertion
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.*
import strikt.assertions.message

class StriktUtilsTest {

    @Test
    fun `satisfies check`() {
        // Given
        val assertion = { assert(false) { "Boom!" } }

        // When
        val result = runCatching {
            expect {
                that("foo").satisfies { assertion() }
            }
        }

        // Then
        expectThat(result).isFailure().and {
            isA<AssertionFailedError>()
            cause.isA<AssertionError>()
            cause.get { this?.message }.isEqualTo("Boom!")
        }
    }

    @Test
    fun `satisfies predicate`() {
        // Given

        // When
        val result = runCatching {
            expect {
                that("foo").satisfies("is equals") { this == "No match" }
            }
        }

        // Then
        expectThat(result).isFailure().and {
            isA<java.lang.AssertionError>()
            cause.get { this?.message }.isEqualTo("Boom!")
        }
    }
}
