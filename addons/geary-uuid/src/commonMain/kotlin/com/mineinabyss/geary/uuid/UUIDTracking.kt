package com.mineinabyss.geary.uuid

import com.mineinabyss.geary.addons.dsl.GearyAddonWithDefault
import com.mineinabyss.geary.uuid.systems.createTrackUUIDOnAddSystem
import com.mineinabyss.geary.uuid.systems.createUntrackUuidOnRemoveSystem
import com.mineinabyss.idofront.di.DI

val uuid2Geary by DI.observe<UUID2GearyMap>()

object UUIDTracking : GearyAddonWithDefault<UUID2GearyMap> {
    override fun default() = SimpleUUID2GearyMap()

    override fun UUID2GearyMap.install() {
        createTrackUUIDOnAddSystem()
        createUntrackUuidOnRemoveSystem()
    }
}
