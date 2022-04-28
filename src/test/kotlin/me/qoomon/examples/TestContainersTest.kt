package me.qoomon.examples

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import strikt.api.expectThat

class TestContainersTest {

    companion object {
        val container by lazy {
            GenericContainer(DockerImageName.parse("alpine")).apply {
                withCommand("nc 127.0.0.1 2323 -lk")
            }
        }

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            container.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            container.stop()
        }
    }

    @Test
    fun `expect valid container state`() {
        // GIVEN

        // WHEN

        // THEN
        expectThat(container.isCreated)
        expectThat(container.isRunning)
    }
}
