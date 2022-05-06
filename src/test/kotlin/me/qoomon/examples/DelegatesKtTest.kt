package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEqualTo
import kotlin.random.Random

internal class DelegatesKtTest {

    @Test
    fun `factory delegation should return result of factory method`() {
        // given
        val factoryValue by factory {
            Random(0).toString()
        }

        // then
        expectThat(factoryValue) isNotEqualTo factoryValue
    }

    @Test
    fun `cacheFactory delegation should return calculated value and remember last value`() {
        // given
        val providerValue by mementoFactory<Int?>(null) {
            when {
                this == null -> 0
                this < 2 -> this + 1
                else -> this
            }
        }

        // then
        expectThat(providerValue) isEqualTo 0
        expectThat(providerValue) isEqualTo 1
        expectThat(providerValue) isEqualTo 2
        expectThat(providerValue) isEqualTo 2
    }
}
