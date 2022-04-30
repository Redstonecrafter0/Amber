package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.renderer.Renderers
import org.joml.Vector2f
import java.awt.Color

interface Widget {

    var isHidden: Boolean
    var x: Int
    var y: Int
    var width: Int
    var height: Int
    var background: Background
    val parent: Widget?

    fun click(x: Double, y: Double, btn: Int) {}
    fun mouseMove(x: Double, y: Double) {}
    fun mouseEnter() {}
    fun mouseLeave() {}
    fun scroll(amount: Double) {}
    fun render(renderers: Renderers, delta: Double) {
        renderers.rectRenderer.render(Vector2f(x.toFloat(), y.toFloat()), Vector2f(width.toFloat(), height.toFloat()), background.x2y1, background.x2y2, background.x1y2, background.x1y1)
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

}

data class PositionedWidget(var x: Int, var y: Int, val widget: Widget)
data class Background(var x1y1: Color, var x2y1: Color, var x1y2: Color, var x2y2: Color) {
    constructor(color: Color = Color(0, 0, 0, 0)) : this(color, color, color, color)
}
