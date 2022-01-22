package com.mineinabyss.geary.minecraft

import com.mineinabyss.geary.ecs.api.engine.Engine
import com.mineinabyss.geary.ecs.api.services.GearyServiceProvider
import com.mineinabyss.geary.ecs.api.services.GearyServices
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.geary.ecs.serialization.withSerialName
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.geary.minecraft.access.BukkitEntityAssociations
import com.mineinabyss.geary.minecraft.access.toGeary
import com.mineinabyss.geary.minecraft.config.GearyConfig
import com.mineinabyss.geary.minecraft.dsl.GearyLoadPhase
import com.mineinabyss.geary.minecraft.dsl.gearyAddon
import com.mineinabyss.geary.minecraft.engine.SpigotEngine
import com.mineinabyss.geary.minecraft.listeners.GearyAttemptSpawnListener
import com.mineinabyss.geary.minecraft.listeners.InheritPrefabsOnLoad
import com.mineinabyss.geary.minecraft.store.FileSystemStore
import com.mineinabyss.geary.minecraft.store.GearyStore
import com.mineinabyss.geary.webconsole.GearyWebConsole
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.serialization.UUIDSerializer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.io.path.div
import kotlin.reflect.KClass

public val gearyPlugin: GearyPlugin = Bukkit.getPluginManager().getPlugin("Geary") as GearyPlugin

public class GearyPlugin : JavaPlugin() {
    override fun onLoad() {
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        instance = this
        registerEvents(StartupEventListener)

        saveDefaultConfig()
        reloadConfig()
        GearyConfig.load()
        GearyServices.setServiceProvider(object : GearyServiceProvider {
            override fun <T : Any> getService(service: KClass<T>): T? {
                return Bukkit.getServer().servicesManager.load(service.java)
            }
        })

        registerService<Engine>(SpigotEngine().apply { start() })

        // Register commands.
        GearyCommands()

        registerEvents(
            BukkitEntityAssociations,
            BukkitAssociations,
            GearyAttemptSpawnListener,
            InheritPrefabsOnLoad(),
        )

        // This will also register a serializer for GearyEntityType
        gearyAddon {
            autoScanAll()

            components {
                //TODO move out to a custom components class
                subclass(UUID::class, UUIDSerializer.withSerialName("geary:uuid"))
                Formats.registerSerialName("geary:uuid", UUID::class)
            }

            dataFolder.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { loadPrefabs(it, namespace = it.name) }

            startup {
                GearyLoadPhase.ENABLE {
                    registerService<GearyStore>(FileSystemStore(dataFolder.toPath() / "serialized"))
                    Bukkit.getOnlinePlayers().forEach { it.toGeary() }
                }
            }
        }
    }

    override fun onDisable() {
        server.scheduler.cancelTasks(this)
    }

    public companion object {
        /** Gets [GearyPlugin] via Bukkit once, then sends that reference back afterwards */
        public lateinit var instance: GearyPlugin
    }
}
