package me.qoomon.examples

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

class JsonbColumnType<T : Any>(
    private val stringify: (T) -> String,
    private val parse: (String) -> T
) : ColumnType() {
    override fun sqlType() = JSONB

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        super.setParameter(
            stmt,
            index,
            value.let {
                PGobject().apply {
                    this.type = sqlType()
                    this.value = value as String?
                }
            })
    }

    override fun valueFromDB(value: Any): Any {
        return if (value is PGobject) {
            value.value.let {
                if (it != null) parse(it)
                else value
            }
        } else value
    }

    override fun valueToString(value: Any?): String = when (value) {
        is Iterable<*> -> nonNullValueToString(value)
        else -> super.valueToString(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun notNullValueToDB(value: Any) = stringify(value as T)

    companion object {
        const val JSONB = "JSONB"
    }
}

fun <T : Any> Table.jsonb(name: String, stringify: (T) -> String, parse: (String) -> T): Column<T> =
    registerColumn(name, JsonbColumnType(stringify, parse))

/**
 * jsonb column with kotlinx.serialization as JSON serializer
 */
fun <T : Any> Table.jsonb(
    name: String,
    serializer: KSerializer<T>,
    json: Json = Json {}
): Column<T> = jsonb(name, { json.encodeToString(serializer, it) }, { json.decodeFromString(serializer, it) })
