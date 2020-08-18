package me.qoomon.examples

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JsonTest {

    @Nested
    inner class KotlinxSerialization {

        @Test
        fun dynamicTests() {
            // Given
            @Serializable
            data class User(val name: String)

            val json = Json {}
            val userJsonInput = """{"name":"John"}""".trimIndent()

            // When
            val user = json.decodeFromString(User.serializer(), userJsonInput)

            val userJsonOutput = json.encodeToString(User.serializer(), user)

            // Then
            expectThat(userJsonOutput).isEqualTo(userJsonInput)
        }
    }
}
