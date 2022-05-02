package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.renderer.Renderers
import java.awt.Color

interface Widget {

    var isHidden: Boolean
    var x: Int
    var y: Int
    var width: Int
    var height: Int
    var background: Color
    val parent: Widget?

    fun click(x: Double, y: Double, btn: Int) {}
    fun mouseMove(x: Double, y: Double) {}
    fun mouseEnter() {}
    fun mouseLeave() {}
    fun scroll(amount: Double) {}
    fun render(renderers: Renderers, delta: Double) {
        renderers.nvg.render {
            fill(rgba(background.red, background.green, background.blue, background.alpha)) {
                rect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
            }
        }
    }

    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

}

data class PositionedWidget(var x: Int, var y: Int, val widget: Widget)
