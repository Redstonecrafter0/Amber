package net.redstonecraft.amber

import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.redstonecraft.amber.commands.commands
import net.redstonecraft.amber.commands.commands.setupAmberCommands
import net.redstonecraft.amber.config.ConfigManager
import net.redstonecraft.amber.events.EventManager
import net.redstonecraft.amber.events.KeyboardKeyEvent
import net.redstonecraft.amber.modules.*
import net.redstonecraft.amber.modules.modules.misc.NarratorDisablerModule
import net.redstonecraft.amber.modules.modules.world.EnvironmentModule
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.File

/**
 * The main class for the mod.
 * */
@Suppress("UNUSED")
object Amber: ModInitializer {

    const val MOD_ID = "amber"
    const val MOD_NAME = "Amber"
    val AUTHORS = listOf("Redstonecrafter0")
    const val VERSION = "0.1.0"

    val dir = File(MinecraftClient.getInstance().runDirectory, "amber")

    val colorScheme = listOf(Color.decode("#121212"), Color.decode("#fdfdfd"), Color.decode("#ffbf00"), Color.decode("#0040ff"))

    val debug = System.getProperty("amber.debug").toBoolean()

    val logger: Logger = LoggerFactory.getLogger(Amber::class.java)

    val modulesToLoad = mutableListOf<() -> BaseModule>()

    override fun onInitialize() {
    }

    @JvmStatic
    fun startup() {
        commands {
            setupAmberCommands()
        }

        EventManager.on<KeyboardKeyEvent> { event ->
            if (MinecraftClient.getInstance().currentScreen == null && event.action == GLFW_RELEASE && event.key != -1) {
                Category.modules.filterIsInstance<BoundModule>().filter { it.key == event.key }.forEach {
                    when (it) {
                        is TriggerModule -> it.run()
                        is ToggleModule -> it.toggle()
                    }
                }
            }
        }

        // Misc
        NarratorDisablerModule

        // World
        EnvironmentModule

        modulesToLoad.forEach { it() }

        ConfigManager.loadById(ConfigManager.currentConfigId)
    }

}
