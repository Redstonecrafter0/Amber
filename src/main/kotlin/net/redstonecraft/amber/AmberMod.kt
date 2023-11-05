package net.redstonecraft.amber

import net.minecraft.client.MinecraftClient
import net.redstonecraft.amber.base.config.AmberConfigManager
import net.redstonecraft.amber.base.event.AmberEventManager
import net.redstonecraft.amber.base.module.AmberModuleManager
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object AmberMod : ModInitializer {

    lateinit var dir: File
        private set

    val logger: Logger = LoggerFactory.getLogger("Amber")
    lateinit var mod: ModContainer

    lateinit var eventManager: AmberEventManager
        private set
    lateinit var moduleManager: AmberModuleManager
        private set
    lateinit var configManager: AmberConfigManager
        private set

    private var initialized = false

    override fun onInitialize(mod: ModContainer) {
        this.mod = mod
    }

    fun init() {
        if (!initialized) {
            dir = MinecraftClient.getInstance().runDirectory.resolve("amber").also { it.mkdirs() }
            eventManager = AmberEventManager()
            moduleManager = AmberModuleManager()
            configManager = AmberConfigManager()
            initialized = true
        }
    }

}
