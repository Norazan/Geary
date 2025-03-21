package com.mineinabyss.geary.actions.event_binds

import com.mineinabyss.geary.actions.ActionGroup
import com.mineinabyss.geary.actions.actions.EnsureAction
import com.mineinabyss.geary.actions.expressions.Expression
import com.mineinabyss.geary.datatypes.ComponentId
import com.mineinabyss.geary.modules.Geary
import com.mineinabyss.geary.serialization.serializers.InnerSerializer
import com.mineinabyss.geary.serialization.serializers.SerializableComponentId
import kotlinx.serialization.Contextual
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmInline

@Serializable(with = EntityObservers.Serializer::class)
class EntityObservers(
    val observers: List<EventBind>,
) {
    class Serializer : InnerSerializer<Map<SerializableComponentId, ActionGroup>, EntityObservers>(
        serialName = "geary:observe",
        inner = MapSerializer(
            ContextualSerializer(ComponentId::class),
            ActionGroup.Serializer()
        ),
        inverseTransform = { TODO() },
        transform = {
            EntityObservers(
                it.map { (event, actionGroup) ->
                    EventBind(event, actionGroup = actionGroup)
                }
            )
        }
    )
}


@Serializable(with = ActionWhen.Serializer::class)
class ActionWhen(val conditions: List<EnsureAction>) {
    class Serializer : InnerSerializer<List<EnsureAction>, ActionWhen>(
        serialName = "geary:when",
        inner = ListSerializer(EnsureAction.serializer()),
        inverseTransform = ActionWhen::conditions,
        transform = { ActionWhen(it) }
    )
}

@JvmInline
@Serializable
value class ActionRegister(val register: String)

@Serializable(with = ActionOnFail.Serializer::class)
class ActionOnFail(val action: ActionGroup) {
    class Serializer : InnerSerializer<ActionGroup, ActionOnFail>(
        serialName = "geary:on_fail",
        inner = ActionGroup.Serializer(),
        inverseTransform = ActionOnFail::action,
        transform = { ActionOnFail(it) }
    )
}

@JvmInline
@Serializable
value class ActionLoop(val expression: String)

@Serializable(with = ActionEnvironment.Serializer::class)
class ActionEnvironment(val environment: Map<String, Expression<@Contextual Any>>) {
    object Serializer : InnerSerializer<Map<String, Expression<@Contextual Any>>, ActionEnvironment>(
        serialName = "geary:with",
        inner = MapSerializer(String.serializer(), Expression.Serializer(ContextualSerializer(Any::class))),
        inverseTransform = ActionEnvironment::environment,
        transform = { ActionEnvironment(it) }
    )
}
