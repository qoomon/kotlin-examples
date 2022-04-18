package me.qoomon.examples

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

class EnumColumnType<T : Enum<T>>(
    private val sqlType: String,
    private val stringify: (T) -> String,
    private val parse: (String) -> T
) : ColumnType() {
    override fun sqlType() = sqlType

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        super.setParameter(
            stmt,
            index,
            value.let {
                PGobject().apply {
                    this.type = sqlType()
                    this.value = value as String?
                }
            }
        )
    }

    override fun valueFromDB(value: Any): Any {
        return if (value is PGobject) parse(value.value!!) else value
    }

    @Suppress("UNCHECKED_CAST")
    override fun notNullValueToDB(value: Any) = stringify(value as T)
}

inline fun <reified T : Enum<T>> Table.enum(
    name: String,
    sqlType: String,
    noinline stringify: (T) -> String = { it.name },
    noinline parse: (String) -> T = { enumValueOf(it) }
): Column<T> =
    registerColumn(
        name = name,
        type = EnumColumnType(sqlType, stringify, parse)
    )
