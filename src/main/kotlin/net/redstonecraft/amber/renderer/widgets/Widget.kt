package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.renderer.Renderers
import net.redstonecraft.amber.renderer.backgrounds.Background

interface Widget {

    var isHidden: Boolean
    var x: Float
    var y: Float
    var width: Float
    var height: Float
    var background: Background?
    val parent: Widget?
    var resizeable: Boolean

    val x2: Float
        get() = x + width
    val y2: Float
        get() = y + height

    val boundsX: ClosedFloatingPointRange<Float>
        get() = x..x2

    val boundsY: ClosedFloatingPointRange<Float>
        get() = y..y2

    operator fun contains(coords: Pair<Number, Number>) = coords.first.toFloat() in boundsX && coords.second.toFloat() in boundsY

    fun click(x: Double, y: Double, btn: Int) {}
    fun mouseMove(x: Double, y: Double) {}
    fun mouseEnter() {}
    fun mouseLeave() {}
    fun scroll(amount: Double) {}
    fun keyPress(keyCode: Int, scanCode: Int, modifiers: Int) {}
    fun keyRelease(keyCode: Int, scanCode: Int, modifiers: Int) {}
    fun charType(chr: Char, modifiers: Int) {}
    fun render(renderers: Renderers, delta: Double) {
        if (!isHidden) background?.render(renderers, delta, x, y, width, height)
    }

    fun resize(width: Float, height: Float) {
        this.width = width
        this.height = height
    }

}
