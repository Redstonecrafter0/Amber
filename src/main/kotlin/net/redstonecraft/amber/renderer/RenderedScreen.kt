package net.redstonecraft.amber.renderer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.redstonecraft.amber.renderer.widgets.ContainerWidget
import net.redstonecraft.amber.renderer.widgets.Widget
import net.redstonecraft.amber.utils.gameOrthoCamTop
import net.redstonecraft.amber.utils.resourceBytes
import net.redstonecraft.amber.utils.resourceText
import net.redstonecraft.opengl.camera.OrthographicCamera
import net.redstonecraft.opengl.render.*

class RenderedScreen {

    private var config: ScreenConfig = ScreenConfig()

    val renderers = Renderers(
        MaskRenderer(gameOrthoCamTop()),
        NanoVGRenderer(MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight),
        SDFFontRenderer(SDFFont(resourceBytes("assets/amber/fonts/jetbrainsmono/image.png"), resourceText("assets/amber/fonts/jetbrainsmono/atlas.json")), 14F, gameOrthoCamTop()),
        TextureRenderer(gameOrthoCamTop())
    )

    fun configure(block: ScreenConfig.() -> Unit) {
        config = ScreenConfig.create(block)
    }

    fun render(delta: Double) {
        config.rootWidget.render(renderers, delta)
        renderers.mask.finish()
        renderers.font.finish()
        renderers.texture.finish()
    }

    fun click(x: Double, y: Double, btn: Int) = config.rootWidget.click(x, y, btn)
    fun mouseMove(x: Double, y: Double) = config.rootWidget.mouseMove(x, y)
    fun scroll(amount: Double) = config.rootWidget.scroll(amount)
    fun keyPress(keyCode: Int, scanCode: Int, modifiers: Int) = config.rootWidget.keyPress(keyCode, scanCode, modifiers)
    fun keyRelease(keyCode: Int, scanCode: Int, modifiers: Int) = config.rootWidget.keyRelease(keyCode, scanCode, modifiers)
    fun charType(chr: Char, modifiers: Int) = config.rootWidget.charType(chr, modifiers)

    fun resize(width: Float, height: Float) {
        val (mask, nvg, font, texture) = renderers
        mask.camera = OrthographicCamera(0F, width, 0F, height)
        nvg.resize(width.toInt(), height.toInt())
        font.camera = OrthographicCamera(0F, width, 0F, height)
        texture.camera = OrthographicCamera(0F, width, 0F, height)
        config.rootWidget.resize(width, height)
    }

}

open class RenderedMinecraftScreen : Screen(LiteralText("Amber Screen")) {

    val screen = RenderedScreen()

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        screen.render(delta.toDouble())
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        screen.click(mouseX, mouseY, button)
        return true
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        screen.mouseMove(mouseX, mouseY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        screen.scroll(amount)
        return true
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        screen.keyPress(keyCode, scanCode, modifiers)
        return true
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        screen.keyRelease(keyCode, scanCode, modifiers)
        return true
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        screen.charType(chr, modifiers)
        return true
    }

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
        screen.resize(width.toFloat(), height.toFloat())
    }

}

class ScreenConfig(val rootWidget: ContainerWidget = ContainerWidget(0F, 0F, MinecraftClient.getInstance().window.framebufferHeight.toFloat(), MinecraftClient.getInstance().window.framebufferWidth.toFloat())) {

    companion object {
        fun create(block: ScreenConfig.() -> Unit): ScreenConfig {
            val config = ScreenConfig()
            config.block()
            return config
        }
    }

}

data class Renderers(
    val mask: MaskRenderer,
    val nvg: NanoVGRenderer,
    val font: SDFFontRenderer,
    val texture: TextureRenderer,
)
