package me.qoomon.examples

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class DummyIT {

    @Test
    fun success() {
        fail { "boom" }
    }
}
