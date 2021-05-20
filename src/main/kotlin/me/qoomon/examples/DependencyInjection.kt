package me.qoomon.examples

import mu.KLogger
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.postgresql.ds.PGSimpleDataSource
import java.net.URL
import java.util.*
import kotlin.concurrent.timer
import kotlin.reflect.KProperty
import kotlin.time.Duration

private val LOG = KotlinLogging.logger {}

open class AppContext(
    private val log: KLogger = LOG
) {

    open val checkQueueUrl by lazy { requireNotNull(System.getenv("CHECK_QUEUE_URL")).let { URL(it) } }
    open val databaseHost by lazy { requireNotNull(System.getenv("DATABASE_HOST")) }
    open val databasePort by lazy { requireNotNull(System.getenv("DATABASE_PORT")).toInt() }
    open val databaseUser by lazy { requireNotNull(System.getenv("DATABASE_USER")) }
    open val databasePassword by lazy { requireNotNull(System.getenv("DATABASE_PASWWORDS")) }
    open val databaseName by lazy { requireNotNull(System.getenv("DATABASE_NAME")) }

    open val database by singleton {
        val datasource = PGSimpleDataSource().apply {
            serverNames = arrayOf(this@AppContext.databaseHost)
            portNumbers = intArrayOf(this@AppContext.databasePort)
            databaseName = this@AppContext.databaseName
            user = this@AppContext.databaseUser
            password = databasePassword
        }
        Database.connect(datasource)
    }
}

fun <T> factory(method: () -> T): Factory<T> = Factory(method)
class Factory<out T>(private val method: () -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = method()
}

fun <T> singleton(initializer: () -> T): Singleton<T> = Singleton(initializer)
class Singleton<out T>(initializer: () -> T) : Lazy<T> by lazy(initializer)

fun schedule(name: String, period: Duration, action: TimerTask.() -> Unit) = timer(
    name = name,
    initialDelay = period.inWholeMilliseconds,
    period = period.inWholeMilliseconds,
    daemon = true,
    action = action,
)
