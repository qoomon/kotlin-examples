package me.qoomon.examples

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import strikt.api.expectThat
import strikt.assertions.isNotNull
import java.util.*

class ExposedJsonColumnTypeTest {

    @Serializable
    data class Permission(val name: String)

    @Serializable
    data class Role(val name: String, val permissions: List<Permission> = emptyList())

    object UsersTable : UUIDTable("users") {
        val name = varchar("name", 50).index()
        val role = jsonb("role", Role.serializer())
        val permissions = jsonb("permissions", Permission.serializer().list)
    }

    class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
        var name by UsersTable.name
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
                role = Role("Admin", listOf(Permission("DB"), Permission("FTP")))
                permissions = listOf(Permission("DB"), Permission("FTP"))
            }
        }

        // When
        transaction(database) {
            addLogger(StdOutSqlLogger)

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
                    UsersTable.role.json<String>("name").eq("Admin")
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.role.json<String>( "permissions", "0", "name").eq("DB")
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }

            run {
                val user = UserEntity.find {
                    UsersTable.role.json<Any>( "permissions", "0").isNotNull()
                }.firstOrNull()

                // Then
                expectThat(user).isNotNull()
                println("User: $user")
                println(" role: ${user?.role}")
                println(" permissions: ${user?.permissions?.joinToString(", ")}")
            }
        }



    }

    companion object {

        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:10.7")

        val database by lazy {
            Database.connect(
                url = postgresContainer.getJdbcUrl(),
                driver = "org.postgresql.Driver",
                user = postgresContainer.getUsername(), password = postgresContainer.getPassword()
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

