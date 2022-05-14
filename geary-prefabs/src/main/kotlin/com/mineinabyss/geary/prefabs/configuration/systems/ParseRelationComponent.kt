package com.mineinabyss.geary.prefabs.configuration.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.components.RelationComponent
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope

@AutoScan
class ParseRelationComponent : GearyListener() {
    private val TargetScope.relation by added<RelationComponent>()

    @Handler
    private fun TargetScope.convertToRelation() {
        entity.setRelation(relation.value, relation.key)
        entity.remove<RelationComponent>()
    }
}
