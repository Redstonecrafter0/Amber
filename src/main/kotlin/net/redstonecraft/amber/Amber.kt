package net.redstonecraft.amber

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import java.awt.Color

/**
 * The main class for the mod.
 * */
@Suppress("UNUSED")
object Amber: ModInitializer {

    const val MOD_ID = "amber"
    const val MOD_NAME = "Amber"
    val AUTHORS = listOf("Redstonecrafter0")

    val colorScheme = Triple(Color.decode("#121212"), Color.decode("#fdfdfd"), Color.decode("#ffbf00"))

    val debug = System.getProperty("amber.debug").toBoolean()

    val logger = LoggerFactory.getLogger(Amber::class.java)

    override fun onInitialize() {
    }

    fun startup() {
    }

}
