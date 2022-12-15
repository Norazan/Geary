package com.mineinabyss.geary.papermc.events

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.idofront.typealiases.BukkitEntity
import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class GearyAttemptMinecraftSpawnEvent(
    val location: Location,
    val prefab: GearyEntity,
) : Event() {
    var bukkitEntity: BukkitEntity? = null

    override fun getHandlers(): HandlerList = handlerList

    internal companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
