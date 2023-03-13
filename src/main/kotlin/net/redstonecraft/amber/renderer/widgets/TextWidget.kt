package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.renderer.Renderers
import net.redstonecraft.amber.renderer.ScreenConfig
import net.redstonecraft.amber.renderer.backgrounds.Background
import java.awt.Color

class TextWidget(
    override var x: Float,
    override var y: Float,
    override var width: Float,
    override var height: Float,
    var text: String,
    var color: Color = Color.WHITE,
    override var background: Background? = null,
    override val parent: Widget? = null
) : Widget {

    override var isHidden: Boolean = false
    override var resizeable: Boolean = false

    override fun render(renderers: Renderers, delta: Double) {
        super.render(renderers, delta)
        renderers.font.render(text, x, y, color)
    }

}

fun ScreenConfig.text(text: String, x: Float, y: Float, width: Float, height: Float, color: Color = Color.WHITE, background: Background? = null) {
    rootWidget += TextWidget(x, y, width, height, text, color, background, rootWidget)
}
