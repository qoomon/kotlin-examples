package me.qoomon.examples

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
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

            val json = Json(JsonConfiguration.Stable)
            val userJsonInput = """{"name":"John"}""".trimIndent()

            // When
            val user = json.parse(User.serializer(), userJsonInput)

            val userJsonOutput = json.stringify(User.serializer(), user)

            // Then
            expectThat(userJsonOutput).isEqualTo(userJsonInput)
        }
    }
}
