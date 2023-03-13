package net.redstonecraft.amber

import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.redstonecraft.amber.commands.commands
import net.redstonecraft.amber.commands.commands.setupAmberCommands
import net.redstonecraft.amber.config.ConfigManager
import net.redstonecraft.amber.events.EventManager
import net.redstonecraft.amber.events.KeyboardKeyEvent
import net.redstonecraft.amber.events.WindowResizeEvent
import net.redstonecraft.amber.events.on
import net.redstonecraft.amber.modules.*
import net.redstonecraft.amber.modules.modules.misc.NarratorDisablerModule
import net.redstonecraft.amber.modules.modules.world.EnvironmentModule
import net.redstonecraft.opengl.buffer.Framebuffer
import net.redstonecraft.opengl.camera.OrthographicCamera
import net.redstonecraft.opengl.render.HorizontalBlurRenderer
import net.redstonecraft.opengl.render.Texture
import net.redstonecraft.opengl.render.VerticalBlurRenderer
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

    var nvgFont = "Jetbrains Mono"
    val blurredGame: Texture
        get() = blurFb2.texture

    @JvmStatic
    lateinit var blurFb1: Framebuffer
        private set
    @JvmStatic
    lateinit var blurFb2: Framebuffer
        private set
    @JvmStatic
    lateinit var horiBlurRenderer: HorizontalBlurRenderer
        private set
    @JvmStatic
    lateinit var vertBlurRenderer: VerticalBlurRenderer
        private set

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

        ConfigManager.loadById(ConfigManager.currentConfigId, true)

        blurFb1 = Framebuffer(MinecraftClient.getInstance().window.framebufferWidth / 2, MinecraftClient.getInstance().window.framebufferHeight / 2)
        blurFb2 = Framebuffer(MinecraftClient.getInstance().window.framebufferWidth / 2, MinecraftClient.getInstance().window.framebufferHeight / 2)
        horiBlurRenderer = HorizontalBlurRenderer(OrthographicCamera(0F, MinecraftClient.getInstance().window.framebufferWidth.toFloat(), MinecraftClient.getInstance().window.framebufferHeight.toFloat(), 0F))
        vertBlurRenderer = VerticalBlurRenderer(OrthographicCamera(0F, MinecraftClient.getInstance().window.framebufferWidth.toFloat(), MinecraftClient.getInstance().window.framebufferHeight.toFloat(), 0F))

        on<WindowResizeEvent> {
            blurFb1.resize(it.width, it.height)
            blurFb2.resize(it.width, it.height)
            horiBlurRenderer.camera = OrthographicCamera(0F, MinecraftClient.getInstance().window.framebufferWidth.toFloat(), MinecraftClient.getInstance().window.framebufferHeight.toFloat(), 0F)
            vertBlurRenderer.camera = OrthographicCamera(0F, MinecraftClient.getInstance().window.framebufferWidth.toFloat(), MinecraftClient.getInstance().window.framebufferHeight.toFloat(), 0F)
        }
    }

}
