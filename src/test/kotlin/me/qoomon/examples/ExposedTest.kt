package me.qoomon.examples

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.isEqualTo

class ExposedTest {

    val shopDAO = mockk<ShopDAO>()
    val subject = ShopService(shopDAO)

    @Test
    fun name() {
        // Give
        shopDAO.apply {
            every { transaction(any<ShopDAO.() -> Any>()) } answers {
                val block = arg<ShopDAO.() -> Any>(0)
                this@apply.block()
            }
            every {
                getShopsByCreationDate(anyInlineValue())
            } returns listOf(
                mockk {
                    every { name } returns "Aldi"
                }
            )
        }

        // When
        val newShops = subject.getNewShops()

        // Then
        expectThat(newShops) {
            get(0).get { name } isEqualTo "Aldi"
        }
    }
}

fun ShopDAO.simulateAldi() {
    every { getShopsByCreationDate(any()) } returns listOf(
        mockk {
            every { name } returns "Aldi"
        }
    )
}
