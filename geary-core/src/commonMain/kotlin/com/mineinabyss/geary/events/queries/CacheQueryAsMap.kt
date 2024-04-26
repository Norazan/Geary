package com.mineinabyss.geary.events.queries

import com.mineinabyss.geary.datatypes.Entity
import com.mineinabyss.geary.events.types.OnFirstSet
import com.mineinabyss.geary.events.types.OnRemove
import com.mineinabyss.geary.modules.GearyModule
import com.mineinabyss.geary.systems.builders.observe
import com.mineinabyss.geary.systems.query.ShorthandQuery

fun <T, Q : ShorthandQuery> GearyModule.cacheGroupedBy(
    query: Q,
    groupBy: ObserverContext.(Q) -> T
): QueryGroupedBy<T, Q> {
    return object : QueryGroupedBy<T, Q>(query, this) {
        override fun ObserverContext.groupBy(query: Q): T = groupBy(query)
    }
}

fun <T, Q : ShorthandQuery> GearyModule.cacheAssociatedBy(
    query: Q,
    associateBy: ObserverContext.(Q) -> T
): QueryAssociatedBy<T, Q> {
    return object : QueryAssociatedBy<T, Q>(query, this) {
        override fun ObserverContext.associateBy(query: Q): T = associateBy(query)
    }
}

abstract class QueryGroupedBy<T, Q : ShorthandQuery>(private val query: Q, geary: GearyModule) {
    private val map = mutableMapOf<T, MutableList<Entity>>()

    abstract fun ObserverContext.groupBy(query: Q): T

    operator fun get(key: T): List<Entity> = map[key] ?: listOf()

    private fun add(key: T, value: Entity) {
        map.getOrPut(key) { mutableListOf() }.add(value)
    }

    private fun remove(key: T, value: Entity) {
        map[key]?.remove(value)
    }

    init {
        geary.observe<OnFirstSet>().involving(query).exec {
            add(groupBy(it), entity)
        }
        geary.observe<OnRemove>().involving(query).exec {
            remove(groupBy(it), entity)
        }
    }
}

abstract class QueryAssociatedBy<T, Q : ShorthandQuery>(private val query: Q, geary: GearyModule) {
    private val map = mutableMapOf<T, Entity>()

    abstract fun ObserverContext.associateBy(query: Q): T

    operator fun get(key: T): Entity? = map[key]
    private operator fun set(key: T, value: Entity) {
        map[key] = value
    }

    fun <R> query(key: T, ifExists: (Q) -> R): R? {
        return map[key]?.let { ifExists(query) }
    }

    init {
        geary.observe<OnFirstSet>().involving(query).exec {
            map[associateBy(it)] = entity
        }
        geary.observe<OnRemove>().involving(query).exec {
            map.remove(associateBy(it))
        }
    }
}
