package me.qoomon.enhancements.kotlin

import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure

internal class StandardKtTest {

    @Test
    fun `applyIf do apply if predicate is true`() {
        // Given
        val subject = mutableListOf<String>()

        // When
        val result = subject.applyIf({ size == 0 }) {
            add("moin")
        }
        // Then
        expectThat(result).containsExactly("moin")
    }

    @Test
    fun `applyIf do not apply if predicate is false`() {
        // Given
        val subject = mutableListOf("hallo")

        // When
        val result = subject.applyIf({ size == 0 }) {
            add("moin")
        }

        // Then
        expectThat(result).containsExactly("hallo")
    }

    @Test
    fun `letIf do let if predicate is true`() {
        // Given
        val subject = "Hello"

        // When
        val result = subject.letIf({ length > 0 }) {
            "$this World"
        }
        // Then
        expectThat(result).isEqualTo("Hello World")
    }

    @Test
    fun `Result_rethrow throws the corresponding exception`() {
        // When
        val result = runCatching {
            runCatching { TODO() }
                .rethrow<CancellationException, _>()
                .rethrow<NotImplementedError, _>()
        }

        // Then
        expectThat(result).isFailure().isA<NotImplementedError>()
    }

    @Test
    fun `runCatchingExcept throws the corresponding exception`() {
        // When
        val result = runCatching {
            runCatchingExcept(
                CancellationException::class,
                NotImplementedError::class,
            ) { TODO() }
        }

        // Then
        expectThat(result).isFailure().isA<NotImplementedError>()
    }
}
