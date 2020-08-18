package me.qoomon.examples

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
    private val dataSerializer: KSerializer<T>
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
