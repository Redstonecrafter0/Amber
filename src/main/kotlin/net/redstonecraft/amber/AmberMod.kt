package net.redstonecraft.amber

import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AmberMod : ModInitializer {

    val logger: Logger = LoggerFactory.getLogger("Amber")

    override fun onInitialize(mod: ModContainer) {
        logger.info("Hello Quilt world from {}!", mod.metadata()?.name())
    }

}
