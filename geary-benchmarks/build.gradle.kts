import kotlinx.benchmark.gradle.JvmBenchmarkTarget
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id("org.jetbrains.kotlinx.benchmark") version "0.4.9"
    kotlin("plugin.allopen") version idofrontLibs.versions.kotlin.get()
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

dependencies {
    implementation(project(":geary-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.9")
}

benchmark {
    configurations {
        named("main") {
            exclude("jvmTesting")
            warmups = 3
            iterations = 3
            iterationTime = 5
            iterationTimeUnit = "sec"
        }

        create("fast") {
            exclude("jvmTesting")
            warmups = 1
            iterations = 1
            iterationTime = 3
            iterationTimeUnit = "sec"
        }

        create("fastest") {
            exclude("jvmTesting")
            warmups = 1
            iterations = 1
            iterationTime = 3
            iterationTimeUnit = "sec"
        }

        create("specific") {
            include("EventCalls")
            warmups = 1
            iterations = 1
            iterationTime = 3
            iterationTimeUnit = "sec"
        }
    }
    targets {
        register("main") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.21"
        }
    }
}
