package me.qoomon.examples

import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import strikt.api.expectThat

@Testcontainers
class TestContainersTest {

    companion object {
        @Container
        val container = GenericContainer(DockerImageName.parse("alpine")).apply {
            withCommand("nc 127.0.0.1 2323 -lk")
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
