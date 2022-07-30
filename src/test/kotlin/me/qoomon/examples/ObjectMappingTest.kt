package me.qoomon.examples

import io.mockk.spyk
import me.qoomon.enhancements.mockk.verifyAllPropertyGetters
import org.junit.jupiter.api.Test

internal class ObjectMappingTest {

    @Test
    fun toDTO() {
        val dummy = spyk(Dummy(foo = "123", bar = 73))

        dummy.toDTO()

        verifyAllPropertyGetters(dummy, except = arrayOf(Dummy::bar))
    }
}

