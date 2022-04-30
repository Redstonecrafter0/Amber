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
    }

    fun click(x: Double, y: Double, btn: Int){
        config.rootWidget.click(x, y, btn)
    }

    fun mouseMove(x: Double, y: Double) {
        config.rootWidget.mouseMove(x, y)
    }

    fun scroll(amount: Double) {
        config.rootWidget.scroll(amount)
    }

    fun resize(width: Int, height: Int) {
        val (mask, nvg, font, texture) = renderers
        mask.camera = OrthographicCamera(0F, width.toFloat(), 0F, height.toFloat())
        nvg.resize(width, height)
        font.camera = OrthographicCamera(0F, width.toFloat(), 0F, height.toFloat())
        texture.camera = OrthographicCamera(0F, width.toFloat(), 0F, height.toFloat())
        config.rootWidget.resize(width, height)
    }

}

open class RenderedMinecraftScreen : Screen(LiteralText("")) {

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

    override fun resize(client: MinecraftClient?, width: Int, height: Int) {
        screen.resize(width, height)
    }

}

class ScreenConfig {

    val rootWidget: Widget = ContainerWidget(0, 0, MinecraftClient.getInstance().window.framebufferHeight, MinecraftClient.getInstance().window.framebufferWidth)

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
