package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.renderer.Renderers
import net.redstonecraft.amber.renderer.ScreenConfig
import net.redstonecraft.amber.renderer.backgrounds.Background

class ContainerWidget(
    override var x: Float,
    override var y: Float,
    override var width: Float,
    override var height: Float,
    override var background: Background? = null,
    override val parent: Widget? = null,
    override var resizeable: Boolean = false
): Widget {

    val children: MutableList<ChildWidget> = mutableListOf()

    override var isHidden: Boolean = false

    fun add(child: ChildWidget) {
        children += child
    }

    fun add(child: Widget) = add(ChildWidget(child))

    operator fun plusAssign(child: ChildWidget) = add(child)
    operator fun plusAssign(child: Widget) = add(child)

    override fun click(x: Double, y: Double, btn: Int) {
        val top = children.firstOrNull { (x to y) in it.widget }
        if (top != null) {
            children -= top
            children.add(0, top)
        }
        children.firstOrNull { (x to y) in it.widget }?.widget?.click(x, y, btn)
    }

    override fun mouseMove(x: Double, y: Double) {
        children
            .filter { !it.isMouseEntered && (x to y) in it.widget }
            .forEach {
                it.isMouseEntered = true
                it.widget.mouseEnter()
            }
        children
            .filter { it.isMouseEntered && (x to y) in it.widget }
            .forEach {
                it.isMouseEntered = false
                it.widget.mouseLeave()
            }
        children
            .filter { it.isMouseEntered }
            .forEach { it.widget.mouseMove(x, y) }
    }

    override fun scroll(amount: Double) {
        children.filter { it.isMouseEntered }.forEach { it.widget.scroll(amount) }
    }

    override fun render(renderers: Renderers, delta: Double) {
        super.render(renderers, delta)
        children
            .filter { !it.widget.isHidden }
            .forEach { it.widget.render(renderers, delta) }
    }
}

fun ScreenConfig.container(x: Float, y: Float, w: Float, h: Float, background: Background? = null, block: ScreenConfig.() -> Unit) {
    val container = ContainerWidget(x, y, w, h, background, rootWidget)
    rootWidget += container
    val subConfig = ScreenConfig(container)
    subConfig.block()
}

data class ChildWidget(val widget: Widget, var isMouseEntered: Boolean = false)
