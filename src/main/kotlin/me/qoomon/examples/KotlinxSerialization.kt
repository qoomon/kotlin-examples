package me.qoomon.examples

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import java.time.Instant

/**
 * handle json fields that can be a single object or an array of those objects.
 * ```json
 * { "foo": { "bar": 1 } } or { "foo": [ { "bar": 1 }, { "buzz": 2 } ] }
 * ```
 */
@Serializer(forClass = List::class)
class SingleElementListSerializer<T : Any>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<List<T>> {
    override val descriptor = buildClassSerialDescriptor(serialName = "SingleElementListSerializer")
    private val dataListSerializer = ListSerializer(dataSerializer)

    override fun deserialize(decoder: Decoder): List<T> {
        decoder as? JsonDecoder ?: throw IllegalStateException(
            "This serializer can be used only with Json format." +
                "Expected Decoder to be JsonInput, got ${this::class}"
        )
        return when (val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())) {
            is JsonArray -> decoder.json.decodeFromJsonElement(dataListSerializer, jsonElement)
            else -> listOf(decoder.json.decodeFromJsonElement(dataSerializer, jsonElement))
        }
    }

    override fun serialize(encoder: Encoder, value: List<T>) {
        return when (value.size) {
            1 -> encoder.encodeSerializableValue(dataSerializer, value.single())
            else -> encoder.encodeSerializableValue(dataListSerializer, value)
        }
    }
}

@Serializer(forClass = Instant::class)
class InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}



@JvmInline
@Serializable
value class ProjectName private constructor(val value: String) {

    init {
        // checkNotNull() -> IllegalArgumentException
        // require -> IllegalStateException
        require(value.length <= 32) { "length is greater than 32" }
    }

    companion object {
        operator fun invoke(value: String): Result<ProjectName> {
            return kotlin.runCatching { ProjectName(value) }
        }
    }
}

@Serializable
data class Project(val name: ProjectName, val language: String)

fun main() {
    // Serializing objects
    val data = Project(ProjectName("kotlinx.serialization").getOrThrow(), "Kotlin")
    val string = Json.encodeToString(data)
    println(string) // {"name":"kotlinx.serialization","language":"Kotlin"}
    // Deserializing back into objects
    val obj = Json.decodeFromString<Project>(string)
    println(obj) // Project(name=kotlinx.serialization, language=Kotlin)
    // invalid project name
    Json.decodeFromString<Project>("{\"name\":\"kotlinx.serializationxxxxxxxxxxxxxxxx\",\"language\":\"Kotlin\"}")
}
