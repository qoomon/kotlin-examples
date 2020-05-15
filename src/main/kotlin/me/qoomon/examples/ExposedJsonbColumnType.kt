package me.qoomon.examples

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.qoomon.examples.JsonbColumnType.Companion.JSONB
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

class JsonbColumnType<T : Any>(
    private val stringify: (T) -> String,
    private val parse: (String) -> T
) : ColumnType() {
    override fun sqlType() = JSONB

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        super.setParameter(stmt, index, value.let {
            PGobject().apply {
                this.type = sqlType()
                this.value = value as String?
            }
        })
    }

    override fun valueFromDB(value: Any): Any {
        return if (value is PGobject) parse(value.value) else value
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
    json: Json = Json(JsonConfiguration.Stable)
): Column<T> = jsonb(name, { json.stringify(serializer, it) }, { json.parse(serializer, it) })

sealed class JsonValueType<T> : IColumnType
object JsonBoolean : JsonValueType<Boolean>(), IColumnType by BooleanColumnType()
object JsonInt : JsonValueType<Int>(), IColumnType by IntegerColumnType()
object JsonFloat : JsonValueType<Float>(), IColumnType by FloatColumnType()
object JsonString : JsonValueType<String>(), IColumnType by TextColumnType()
object JsonObject : JsonValueType<Any>(), IColumnType by JsonbColumnType(
    { error("Unexpected call") }, { error("Unexpected call") })

class JsonExpression<T>(
    val expr: Expression<*> ,
    val path: List<String>,
    override val columnType: JsonValueType<T>
) : ExpressionWithColumnType<T>() {

    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        if (columnType.sqlType() != JSONB) {
            append("(")
            append(expr)
            if (path.isNotEmpty()) append(" #>> '{${path.joinToString { escapeFieldName(it) }}}'")
            append(")::${columnType.sqlType()}")
        } else {
            append(expr)
            if (path.isNotEmpty()) append(" #> '{${path.joinToString { escapeFieldName(it) }}}'")
        }
    }

    companion object {

        private fun escapeFieldName(value: String) = value.map {
            fieldNameCharactersToEscape[it] ?: it
        }.joinToString("").let { "\"$it\"" }

        private val fieldNameCharactersToEscape = mapOf(
            // '\"' to "\'\'", // no need to escape single quote as we put string in double quotes
            '\"' to "\\\"",
            '\r' to "\\r",
            '\n' to "\\n"
        )
    }
}

fun ExpressionWithColumnType<*>.jsonObject(vararg jsonPath: String) =
    JsonExpression(this, jsonPath.toList(), JsonObject)

fun ExpressionWithColumnType<*>.jsonBoolean(vararg jsonPath: String) =
    JsonExpression(this, jsonPath.toList(), JsonBoolean)

fun ExpressionWithColumnType<*>.jsonInt(vararg jsonPath: String) =
    JsonExpression(this, jsonPath.toList(), JsonInt)

fun ExpressionWithColumnType<*>.jsonFloat(vararg jsonPath: String) =
    JsonExpression(this, jsonPath.toList(), JsonFloat)

fun ExpressionWithColumnType<*>.jsonString(vararg jsonPath: String) =
    JsonExpression(this, jsonPath.toList(), JsonString)


class JsonExistsOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "??")

/** Checks if this expression contains some [t] value. */
infix fun <T> JsonExpression<Any>.exists(t: T): JsonExistsOp =
    JsonExistsOp(this, SqlExpressionBuilder.run { this@exists.wrap(t) })

/** Checks if this expression contains some [other] expression. */
infix fun <T> JsonExpression<Any>.exists(other: Expression<T>): JsonExistsOp =
    JsonExistsOp(this, other)


class JsonContainsOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "@>") {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        super.toQueryBuilder(this)
        append("::jsonb")
    }
}

/** Checks if this expression contains some [t] value. */
infix fun <T> JsonExpression<Any>.contains(t: T): JsonContainsOp =
    JsonContainsOp(this, SqlExpressionBuilder.run { this@contains.wrap(t) })

/** Checks if this expression contains some [other] expression. */
infix fun <T> JsonExpression<Any>.contains(other: Expression<T>): JsonContainsOp =
    JsonContainsOp(this, other)


class QueryOp(val query: String) : Op<Boolean>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append(query)
    }
}

fun SqlExpressionBuilder.where(query: String) = QueryOp(query)

