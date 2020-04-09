package me.qoomon.examples

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.quartz.CronExpression
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant());
fun Date.toLocalDate(): LocalDateTime = Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime()

class ScheduleTest {

    @TestFactory
    fun `quartz CronExpression`(): List<DynamicTest> {
        return dynamicTests({
            // Given
            val cronExpression = CronExpression(cronString)

            // When
            val execute = when (lastExecution) {
                null -> true
                else -> when (val nextExecution =
                    cronExpression.getNextValidTimeAfter(lastExecution.toDate())?.toLocalDate()) {
                    null -> false
                    else -> now.isAfter(nextExecution)
                }
            }

            // Then
            expectThat(execute).isEqualTo(expectedExecute)

        }) {
            data class TestCase(
                val cronString: String,
                val now: LocalDateTime,
                val lastExecution: LocalDateTime,
                val expectedExecute: Boolean
            )

            val now = LocalDateTime.parse("2020-01-01T00:00")
            listOf(
                TestCase("0 * * * * ?", now, now.minusMinutes(10), true),
                TestCase("0 0 * * * ?", now, now.minusMinutes(10), false)
            )
        }
    }

    @TestFactory
    fun `cron-utils CronExpression`(): List<DynamicTest> {
        return dynamicTests({
            // Given
            var cronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
            val cronExpression = cronParser.parse(cronString)!!
            val cronExecutionTime = ExecutionTime.forCron(cronExpression)!!

            // When
            val execute = when (lastExecution) {
                null -> true
                else -> when (val nextExecution = cronExecutionTime.nextExecution(lastExecution).orElse(null) ?: null) {
                    null -> false
                    else -> now.isAfter(nextExecution)
                }
            }

            // Then
            expectThat(execute).isEqualTo(expectedExecute)

        }) {
            data class TestCase(
                val cronString: String,
                val now: ZonedDateTime,
                val lastExecution: ZonedDateTime,
                val expectedExecute: Boolean
            )

            val now = ZonedDateTime.parse("2020-01-01T00:00:00Z")
            listOf(
                TestCase("0 * * * * ?", now, now.minusMinutes(10), true),
                TestCase("0 0 * * * ?", now, now.minusSeconds(10), false)
            )
        }
    }
}
