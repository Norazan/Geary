package com.mineinabyss.geary.modules

import co.touchlab.kermit.Logger
import com.mineinabyss.geary.datatypes.maps.HashTypeMap
import com.mineinabyss.geary.datatypes.maps.TypeMap
import com.mineinabyss.geary.engine.*
import com.mineinabyss.geary.engine.archetypes.*
import com.mineinabyss.geary.engine.archetypes.operations.ArchetypeMutateOperations
import com.mineinabyss.geary.engine.archetypes.operations.ArchetypeReadOperations
import com.mineinabyss.idofront.di.DI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

val archetypes: GearyArchetypeModule by DI.observe()

data class GearyArchetypeModule(
    val tickDuration: Duration = 50.milliseconds,
) : GearyModule {
    override val logger = Logger.withTag("Geary")
    override val queryManager = ArchetypeQueryManager()

    override val components by lazy { Components() }

    override val engine = ArchetypeEngine(tickDuration)
    override val eventRunner = ArchetypeEventRunner()
    override val pipeline get() = PipelineImpl()

    override val read = ArchetypeReadOperations()
    override val write = ArchetypeMutateOperations()
    override val entityProvider = EntityByArchetypeProvider()
    override val componentProvider = ComponentAsEntityProvider()

    val records = HashTypeMap()
    val archetypeProvider = SimpleArchetypeProvider()

    override fun inject() {
        DI.add<GearyModule>(this)
        DI.add(this)
    }

    override fun start() {
        componentProvider.createComponentInfo()
        engine.start()
    }
}
