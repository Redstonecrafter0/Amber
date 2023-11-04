package net.redstonecraft.amber

import net.minecraft.client.MinecraftClient
import net.redstonecraft.amber.base.config.AmberConfigManager
import net.redstonecraft.amber.base.module.AmberModuleManager
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AmberMod : ModInitializer {

    val dir = MinecraftClient.getInstance().runDirectory.resolve("amber").also { it.mkdirs() }

    val logger: Logger = LoggerFactory.getLogger("Amber")
    lateinit var mod: ModContainer

    val moduleManager = AmberModuleManager()
    val configManager = AmberConfigManager()

    override fun onInitialize(mod: ModContainer) {
        this.mod = mod
    }

}
