package com.mineinabyss.geary.events

import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.helpers.tests.GearyTest
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.systems.listener
import com.mineinabyss.geary.systems.query.ListenerQuery
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SourceTargetEventTest : GearyTest() {
    class Strength(val amount: Int)
    class Attack
    data class Health(val amount: Int)

    fun interactionListener() = geary.listener(object : ListenerQuery() {
        val strength by source.get<Strength>()
        var health by target.get<Health>()
    }.apply { event.match { has<Attack>() } }) {
        health = Health(health.amount - strength.amount)
    }

    @Test
    fun interactions() {
        interactionListener()
        val source = entity {
            set(Strength(10))
        }
        val target = entity {
            set(Health(10))
        }
        target.get<Health>()?.amount shouldBe 10
        target.callEvent(Attack(), source = source)
        target.get<Health>()?.amount shouldBe 0
        target.callEvent(Attack())
        target.get<Health>()?.amount shouldBe 0
        target.callEvent(source = source)
        target.get<Health>()?.amount shouldBe 0
    }
}
