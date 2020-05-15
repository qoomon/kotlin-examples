package me.qoomon.examples

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.jdbc.iterate
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import strikt.api.expectThat
import strikt.assertions.isNotNull
import java.util.*

class ExposedJsonbColumnTypeTest {

    @Serializable
    data class Permission(val name: String)

    @Serializable
    data class Role(val name: String, val permissions: List<Permission> = emptyList())

    object UsersTable : UUIDTable("users") {
        val name = varchar("name", 50).index()
        val scopes = jsonb("scopes", String.serializer().list)
        val role = jsonb("role", Role.serializer())
        val permissions = jsonb("permissions", Permission.serializer().list)
    }

    class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
        var name by UsersTable.name
        var scopes by UsersTable.scopes
        var role by UsersTable.role
        var permissions by UsersTable.permissions

        companion object : UUIDEntityClass<UserEntity>(UsersTable)
    }

    @Test
    fun `jsonb column`() {
        // Given
        val userEntity = transaction(database) {
            addLogger(StdOutSqlLogger)
            UserEntity.new {
                name = "John"
                scopes = listOf("auth", "booking")
                role = Role("Admin", permissions = listOf(Permission("DB"), Permission("FTP")))
                permissions = listOf(Permission("DB"), Permission("FTP"))
            }
        }

        // When
        transaction(database) {

            addLogger(StdOutSqlLogger)

//            run {
//
//                val user = UserEntity.find {
//                   where("${ UsersTable.role.nameInDatabaseCase()} #> '{\"permissions\"}' @> '[{\"name\":\"DB\"}]'::jsonb")
//                }.firstOrNull()
//
//                // Then
//                expectThat(user).isNotNull()
//                println("User: $user")
//                println(" role: ${user?.role}")
//                println(" permissions: ${user?.permissions?.joinToString(", ")}")
//            }

            run {

                val user = UserEntity.find {
                    UsersTable.role.jsonObject("permissions").contains("[{\"name\":\"DB\"}]")
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.scopes.jsonObject().exists("auth")
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }


            run {
                val user = UserEntity.findById(userEntity.id)

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.role.jsonString("name").eq("Admin")
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.role.jsonString("permissions", "0", "name").eq("DB")
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.role.jsonObject("permissions", "0").isNotNull()
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.role.jsonObject().isNotNull()
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }
        }
    }

    @Test
    fun `jsonb column 2`() {
        // Given
        val userEntity = transaction(database) {
            addLogger(StdOutSqlLogger)
            UserEntity.new {
                name = "John"
                scopes = listOf("auth", "booking")
                role = Role("Admin", listOf(Permission("DB"), Permission("FTP")))
                permissions = listOf(Permission("DB"), Permission("FTP"))
            }
        }

        // When
        transaction(database) {

            addLogger(StdOutSqlLogger)

            run {
                exec("""SELECT users.* FROM users WHERE users."scopes" ?? 'auth'""") {
                    val metaData = it.metaData
                    val columnCount = metaData.columnCount

                    val header = (1..columnCount).map { metaData.getColumnLabel(it) }.joinToString()
                    println("header: $header")

                    it.iterate {
                        val row = (1..columnCount).map { getString(it) }.joinToString()
                        println("row: $row")
                    }
                }
            }
        }
    }

    companion object {

        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:10.7")

        val database by lazy {
            Database.connect(
                url = postgresContainer.jdbcUrl,
                driver = "org.postgresql.Driver",
                user = postgresContainer.username, password = postgresContainer.password
            )
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgresContainer.start()
            transaction(database) {
                SchemaUtils.create(UsersTable)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            postgresContainer.stop()
        }
    }
}
