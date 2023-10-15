package net.redstonecraft.amber

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AmberMod : ModInitializer {

    val logger: Logger = LoggerFactory.getLogger("Amber")
    lateinit var mod: ModContainer

    override fun onInitialize(mod: ModContainer) {
        this.mod = mod
        logger.info("Hello Quilt world from {}!", mod.metadata()?.name())
    }

}
