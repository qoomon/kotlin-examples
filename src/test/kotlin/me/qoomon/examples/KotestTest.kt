package me.qoomon.examples

import io.kotest.core.spec.style.BehaviorSpec

class Kotest : BehaviorSpec({

    Given("a broomstick") {
        When("I throw it away") {
            Then("it should come back") {
                // test code
            }
        }
        When("I sit on it") {
            Then("I should be able to fly") {
                // test code
            }
        }
    }
})
