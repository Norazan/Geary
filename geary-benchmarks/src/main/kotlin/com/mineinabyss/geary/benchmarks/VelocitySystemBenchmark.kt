package com.mineinabyss.geary.benchmarks

import com.mineinabyss.geary.benchmarks.helpers.oneMil
import com.mineinabyss.geary.benchmarks.helpers.tenMil
import com.mineinabyss.geary.helpers.entity
import com.mineinabyss.geary.modules.TestEngineModule
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.Pointer
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class VelocitySystemBenchmark {
    data class Velocity(val x: Float, val y: Float)
    data class Position(val x: Float, val y: Float)

    object VelocitySystem : RepeatingSystem() {
        private val Pointer.velocity by get<Velocity>()
        private var Pointer.position by get<Position>()

        override fun Pointer.tick() {
            position = Position(position.x + velocity.x, position.y + velocity.y)
        }
    }

    @Setup
    fun setUp() {
        geary(TestEngineModule)

        repeat(tenMil) {
            entity {
                set(Velocity(it.toFloat() / oneMil, it.toFloat() / oneMil))
                set(Position(0f, 0f))
            }
        }
    }

    // 0.606 ops/s
    @Benchmark
    fun velocitySystem() {
        VelocitySystem.tickAll()
    }
}

fun main() {
    VelocitySystemBenchmark().apply {
        setUp()
        velocitySystem()
    }
}
