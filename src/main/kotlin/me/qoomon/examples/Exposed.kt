@file:Suppress("MagicNumber")

package me.qoomon.examples

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder.DESC
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

object Shops : UUIDTable("shops") {
    val createdDate = timestamp("created_date")
        .defaultExpression(CustomFunction("NOW", JavaInstantColumnType()))

    val name = varchar("name", 32)
    val state = enumerationByName("status", 10, ShopStatus::class)
}

class Shop(id: EntityID<UUID>) : UUIDEntity(id) {
    var createdDate by Shops.createdDate

    var name by Shops.name
    var state by Shops.state

    companion object : UUIDEntityClass<Shop>(Shops)
}

/**
 * Testable / Mockable DAO
 *
 * @see ExposedTest
 *
 * **Usage**
 * ```kotlin
 * val database: Database = ...
 * val shopDAO = ShopDAO(database)
 * shopDAO.transaction {
 *     getNewShops()
 * }
 * ```
 */
class ShopDAO(private val database: Database) {

    fun <T> transaction(block: ShopDAO.() -> T): T = transaction(database) {
        block()
    }

    fun getShopsByCreationDate(withinLast: Duration): Iterable<Shop> = Shop
        .find {
            Shops.createdDate.greater(withinLast.ago())
        }
        .orderBy(Shops.createdDate to DESC)
        .limit(10)
}

class ShopService(private val shopDAO: ShopDAO) {

    fun getNewShops(): List<Shop> {
        return shopDAO.transaction {
            getShopsByCreationDate(7.days).toList()
        }
    }
}

enum class ShopStatus {
    OPEN,
    CLOSED,
}
