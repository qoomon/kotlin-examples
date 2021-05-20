package me.qoomon.examples

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@Serializable
data class UserData(
    val rank: String,
    val score: Int,
)

object UsersTable : Table() {
    val id = varchar("id", length = 10)
    val name = varchar("name", length = 50)
    val data = jsonb("data", UserData.serializer())

    init {
        index(false, Column<String>(this, "(data ->> 'rank')", VarCharColumnType()))
    }
}

class ExposedJsonbColumnTypeTest {

    @Test
    fun test() {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            println("------------")
            println(SchemaUtils.createStatements(UsersTable))
        }
    }

//    @BeforeEach
//    fun beforeEach() {
//        transaction(database) {
//            SchemaUtils.create(*tables.toTypedArray())
//        }
//    }
//
//    @AfterEach
//    fun afterEach() {
//        transaction(database) {
//            SchemaUtils.drop(*tables.toTypedArray())
//        }
//    }
//
    companion object {
        private val postgresContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:11"))
        val database by lazy {
            Database.connect(
                url = postgresContainer.jdbcUrl,
                user = postgresContainer.username,
                password = postgresContainer.password,
            )
        }

        val tables = listOf(
            UsersTable,
        )

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgresContainer.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            postgresContainer.stop()
        }
    }
}
