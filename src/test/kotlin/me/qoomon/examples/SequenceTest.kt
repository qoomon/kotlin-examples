package me.qoomon.examples

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.elementAt
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

class SequenceTest {

    @Test
    fun sequence() {
        // Given
        data class Response(val body: String, val nextToken: String?)

        val givenPageCount = 3

        val apiResponses = (0..givenPageCount).map { pageIndex ->
            Response(
                "body-$pageIndex",
                if (pageIndex + 1 < givenPageCount) "nextToken-${pageIndex + 1}" else null
            )
        }.iterator()

        // When
        val pages = sequence {
            do {
                val response = apiResponses.next()
                yield(response)
            } while (response.nextToken != null)
        }.toList()

        // Then
        expectThat(pages) {
            hasSize(givenPageCount)
            for (index in 0 until givenPageCount) {
                elementAt(index).and {
                    get { body }.isEqualTo("body-$index")
                    val expectedNextToken = if (index + 1 < givenPageCount)
                        "nextToken-${index + 1}" else null
                    get { nextToken }.isEqualTo(expectedNextToken)
                }
            }
        }
    }

    @Test
    fun generateSequence() {
        // Given
        data class Response(val body: String, val nextToken: String?)

        val givenPageCount = 3

        val apiResponses = (0..givenPageCount).map { pageIndex ->
            Response(
                "body-$pageIndex",
                if (pageIndex + 1 < givenPageCount) "nextToken-${pageIndex + 1}" else null
            )
        }.iterator()

        // When
        val seedResponse = apiResponses.next()
        val pages = generateSequence(seedResponse) { lastResponse ->
            if (lastResponse.nextToken != null) {
                apiResponses.next()
            } else null
        }.toList()

        // Then
        expectThat(pages) {
            hasSize(givenPageCount)
            for (index in 0 until givenPageCount) {
                elementAt(index).and {
                    get { body }.isEqualTo("body-$index")
                    val expectedNextToken = if (index + 1 < givenPageCount)
                        "nextToken-${index + 1}" else null
                    get { nextToken }.isEqualTo(expectedNextToken)
                }
            }
        }
    }
}
