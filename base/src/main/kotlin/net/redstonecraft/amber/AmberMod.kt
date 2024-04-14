package net.redstonecraft.amber

import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.redstonecraft.amber.base.config.AmberConfigManager
import net.redstonecraft.amber.base.event.AmberEventManager
import net.redstonecraft.amber.base.module.AmberModuleManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object AmberMod : ModInitializer {

    lateinit var dir: File
        private set

    const val mod = "amber"

    val logger: Logger = LoggerFactory.getLogger("Amber")

    val eventManager = AmberEventManager()
    lateinit var moduleManager: AmberModuleManager
        private set
    lateinit var configManager: AmberConfigManager
        private set

    private var initialized = false

    override fun onInitialize() {
    }

    fun init() {
        if (!initialized) {
            dir = MinecraftClient.getInstance().runDirectory.resolve("amber").also { it.mkdirs() }
            moduleManager = AmberModuleManager()
            configManager = AmberConfigManager()
            try {
                val extraLoader = Class.forName("net.redstonecraft.amber.extra.Loader")
                extraLoader.getMethod("init").invoke(extraLoader.kotlin.objectInstance)
            } catch (_: ClassNotFoundException) {
                // only amber base present
            }
            initialized = true
        }
    }

}
