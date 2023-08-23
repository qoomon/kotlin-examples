package me.qoomon.examples

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isEqualTo

class KotlinxSerializationTest {

    @Nested
    inner class SingleElementListSerializerTest {

        private val json = Json {}

        @Test
        fun `deserialize returns array if field is a json array`() {
            // Given
            val jsonString = """{"singleObjectList":["foo","bar"]}"""

            // When
            val jsonObject = json.decodeFromString(DummyJsonObject.serializer(), jsonString)

            // Then
            expectThat(jsonObject) {
                get { singleObjectList } containsExactlyInAnyOrder (listOf("foo", "bar"))
            }
        }

        @Test
        fun `deserialize returns array if field is a json primitive`() {
            // Given
            val jsonString = """{"singleObjectList":"foo"}"""

            // When
            val jsonObject = json.decodeFromString(DummyJsonObject.serializer(), jsonString)

            // Then
            expectThat(jsonObject) {
                get { singleObjectList } containsExactlyInAnyOrder (listOf("foo"))
            }
        }

        @Test
        fun `serialize should serialize list with multiple element as json array`() {
            // Given
            val jsonObject = DummyJsonObject(
                listOf("foo", "bar"),
            )

            // When
            val jsonString = json.encodeToString(DummyJsonObject.serializer(), jsonObject)

            // Then
            expectThat(jsonString) isEqualTo """{"singleObjectList":["foo","bar"]}"""
        }

        @Test
        fun `serialize should serialize list with one element as json element`() {
            // Given
            val jsonObject = DummyJsonObject(
                listOf("foo"),
            )

            // When
            val jsonString = json.encodeToString(DummyJsonObject.serializer(), jsonObject)

            // Then
            expectThat(jsonString) isEqualTo """{"singleObjectList":"foo"}"""
        }
    }
}

@Serializable
internal data class DummyJsonObject(
    @Serializable(with = SingleElementListSerializer::class)
    val singleObjectList: List<String>,
)
