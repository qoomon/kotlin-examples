import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonInput

@Serializer(forClass = List::class)
class SingleElementListSerializer<T : Any>(private val dataSerializer: KSerializer<T>) : KSerializer<List<T>> {

    override val descriptor: SerialDescriptor = SerialDescriptor(serialName = "SingleElementListSerializer")

    private val dataListSerializer = ListSerializer(dataSerializer)

    override fun deserialize(decoder: Decoder): List<T> {
        decoder as? JsonInput ?: throw IllegalStateException(
                "This serializer can be used only with Json format." +
                    "Expected Decoder to be JsonInput, got ${this::class}"
            )
        return when (val jsonElement = decoder.decodeJson()) {
            is JsonArray -> decoder.json.fromJson(dataListSerializer, jsonElement)
            else -> listOf(decoder.json.fromJson(dataSerializer, jsonElement))
        }
    }

    override fun serialize(encoder: Encoder, value: List<T>) {
        return when (value.size) {
            1 -> encoder.encode(dataSerializer, value.single())
            else -> encoder.encode(dataListSerializer, value)
        }
    }
}
