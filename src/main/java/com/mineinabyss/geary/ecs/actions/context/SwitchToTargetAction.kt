package com.mineinabyss.geary.ecs.actions.context

import com.mineinabyss.geary.ecs.GearyEntity
import com.mineinabyss.geary.ecs.actions.GearyAction
import com.mineinabyss.geary.ecs.components.Target
import com.mineinabyss.geary.ecs.components.get
import com.mineinabyss.geary.ecs.serialization.FlatSerializer
import com.mineinabyss.geary.ecs.serialization.FlatWrap
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Used to execute actions on a different [GearyEntity].
 *
 * @param run The actions to run on the other [GearyEntity].
 */
@Serializable(with = SwitchToTargetSerializer::class)
public class SwitchToTargetAction(
    override val wrapped: List<GearyAction>
) : GearyAction(), FlatWrap<List<GearyAction>> {
    override fun runOn(entity: GearyEntity): Boolean {
        val target = entity.get<Target>()?.entity ?: return false

        return wrapped.count{it.runOn(target)} != 0
    }
}

public object SwitchToTargetSerializer : FlatSerializer<SwitchToTargetAction, List<GearyAction>>(
    "on.target", serializer(), { SwitchToTargetAction(it) }
)
