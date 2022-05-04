package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.Amber
import net.redstonecraft.amber.renderer.Renderers
import net.redstonecraft.amber.renderer.ScreenConfig
import net.redstonecraft.amber.renderer.backgrounds.Background
import net.redstonecraft.amber.utils.lighten
import org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER
import java.awt.Color

class Button(
    override var x: Float,
    override var y: Float,
    override var width: Float,
    override var height: Float,
    var text: String,
    val action: () -> Unit,
    var color: Color,
    var hoverColor: Color,
    var activeColor: Color,
    override var background: Background?,
    override val parent: Widget?
) : Widget {

    override var isHidden: Boolean = false
    override var resizeable: Boolean = false

    var isHovered = false
    var isActive = false

    override fun click(x: Double, y: Double, btn: Int) {
        action()
    }

    override fun render(renderers: Renderers, delta: Double) {
        super.render(renderers, delta)
        renderers.nvg.render {
            val color = when {
                isHovered -> hoverColor.toNvg()
                isActive -> activeColor.toNvg()
                else -> color.toNvg()
            }
            fill(color) {
                moveTo(x, y)
                lineTo(x + width, y)
                lineTo(x + width, y + height)
                lineTo(x, y + height)
                lineTo(x, y)
            }
            font(Amber.nvgFont, color, align = NVG_ALIGN_CENTER) {
                scissor(x, y, width, height)
                textBox(x, y, width, text)
            }
        }
    }
}

fun ScreenConfig.button(x: Float, y: Float, w: Float, h: Float, text: String, background: Background? = null, color: Color = Amber.colorScheme[0], hoverColor: Color = Amber.colorScheme[2], activeColor: Color = Amber.colorScheme[2].lighten(.1F), action: () -> Unit) {
    rootWidget += Button(x, y, w, h, text, action, color, activeColor, hoverColor, background, rootWidget)
}
