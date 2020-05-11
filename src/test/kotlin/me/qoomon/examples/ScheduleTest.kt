package me.qoomon.examples

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import org.junit.jupiter.api.TestFactory
import org.quartz.CronExpression
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
fun Date.toLocalDate(): LocalDateTime = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime()

class ScheduleTest {

    @TestFactory
    fun `quartz CronExpression`() = parameterizedTest(test = {

        // Given
        val cronExpression = CronExpression(given.cronString)

        // When
        val execute = when (given.lastExecution) {
            null -> true
            else -> when (val nextExecution =
                cronExpression.getNextValidTimeAfter(given.lastExecution.toDate())?.toLocalDate()) {
                null -> false
                else -> given.now.isAfter(nextExecution)
            }
        }

        // Then
        expectThat(execute).isEqualTo(expected.execute)
    },
        cases = {
            data class Given(val cronString: String, val now: LocalDateTime, val lastExecution: LocalDateTime?)
            data class Expected(val execute: Boolean)
            data class Case(val given: Given, val expected: Expected)

            val now = LocalDateTime.parse("2020-01-01T00:00")

            listOf(
                Case(
                    Given("0 * * * * ?", now, now.minusMinutes(10)),
                    Expected(true)
                ),
                Case(
                    Given("0 0 * * * ?", now, now.minusMinutes(10)),
                    Expected(false)
                )
            )
        })

    @TestFactory
    fun `quartz CronExpression  2`() = parameterizedTest(test = {

        // Given
        val cronExpression = CronExpression(given.cronString)

        // When
        val execute = when (given.lastExecution) {
            null -> true
            else -> when (val nextExecution =
                cronExpression.getNextValidTimeAfter(given.lastExecution.toDate())?.toLocalDate()) {
                null -> false
                else -> given.now.isAfter(nextExecution)
            }
        }

        // Then
        expectThat(execute).isEqualTo(expected.execute)
    },
        cases = {
            data class Given(val cronString: String, val now: LocalDateTime, val lastExecution: LocalDateTime?)
            data class Expected(val execute: Boolean)
            data class Case(val given: Given, val expected: Expected)

            val now = LocalDateTime.parse("2020-01-01T00:00")

            listOf(
                Case(
                    Given("0 * * * * ?", now, now.minusMinutes(10)),
                    Expected(true)
                ),
                Case(
                    Given("0 0 * * * ?", now, now.minusMinutes(10)),
                    Expected(false)
                )
            )
        })

    @TestFactory
    fun `cron-utils CronExpression`() = parameterizedTest(test = {

        // Given
        val cronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
        val cronExpression = cronParser.parse(given.cronString)!!
        val cronExecutionTime = ExecutionTime.forCron(cronExpression)!!

        // When
        val execute = when (given.lastExecution) {
            null -> true
            else -> when (val nextExecution =
                cronExecutionTime.nextExecution(given.lastExecution).orElse(null) ?: null) {
                null -> false
                else -> given.now.isAfter(nextExecution)
            }
        }

        // Then
        expectThat(execute).isEqualTo(expected.execute)
    },
        cases = {
            data class Given(val cronString: String, val now: ZonedDateTime, val lastExecution: ZonedDateTime?)
            data class Expected(val execute: Boolean)
            data class TestCase(val given: Given, val expected: Expected)

            val now = ZonedDateTime.parse("2020-01-01T00:00:00Z")
            listOf(
                TestCase(
                    Given("0 * * * * ?", now, now.minusMinutes(10)),
                    Expected(true)
                ),
                TestCase(
                    Given("0 0 * * * ?", now, now.minusSeconds(10)),
                    Expected(false)
                )
            )
        })
}
