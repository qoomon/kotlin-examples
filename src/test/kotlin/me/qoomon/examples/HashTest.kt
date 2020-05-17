package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasLength
import strikt.assertions.isEqualTo
import strikt.assertions.length
import java.math.BigInteger
import java.security.MessageDigest

internal class HashTest {

    @Test
    fun hash() {
        // Given
        val source = "Hello World!"

        // When
        val hash = source.hash("SHA-256")

        // Then
        expectThat(hash) isEqualTo "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
    }

    @Test
    fun `hash - padding`() {
        // Given
        val source = "eg3TbYWOxZ"

        // When
        val hash = source.hash("SHA-256")

        // Then
        expectThat(hash) isEqualTo "0d91c8bd0e53a69d2fe09723b4f12d72df02e9b07a029e724d139d0c1b21628f"
    }
}
