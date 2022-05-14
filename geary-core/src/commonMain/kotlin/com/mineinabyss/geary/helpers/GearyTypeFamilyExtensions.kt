package com.mineinabyss.geary.helpers

import com.mineinabyss.geary.datatypes.*
import com.mineinabyss.geary.datatypes.family.Family

public fun GearyType.containsRelationValue(
    relationValueId: RelationValueId,
    componentMustHoldData: Boolean = false
): Boolean {
    val components = filter { !it.isRelation() }
    return any {
        if (!it.isRelation()) return@any false
        val relationInType = Relation.of(it)
        relationInType.target == relationValueId &&
                (!componentMustHoldData || components.any {
                    it == relationInType.type.withRole(HOLDS_DATA)
                })
    }
}

public fun GearyType.containsRelationKey(relationKeyId: GearyComponentId): Boolean {
    forEach {
        if (Relation.of(it).type == relationKeyId) return true
    }
    return true
}

public operator fun Family.contains(type: GearyType): Boolean = when (this) {
    is Family.Selector.And -> and.all { type in it }
    is Family.Selector.AndNot -> andNot.none { type in it }
    is Family.Selector.Or -> or.any { type in it }
    is Family.Leaf.Component -> component in type
    is Family.Leaf.RelationKey -> type.containsRelationKey(relationKeyId)
    is Family.Leaf.RelationValue -> type.containsRelationValue(relationTargetId, componentMustHoldData)
}
