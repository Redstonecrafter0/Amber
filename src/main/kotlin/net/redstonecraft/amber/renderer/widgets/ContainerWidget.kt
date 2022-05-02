package net.redstonecraft.amber.renderer.widgets

import net.redstonecraft.amber.renderer.Renderers
import java.awt.Color

class ContainerWidget(
    override var x: Int,
    override var y: Int,
    override var width: Int,
    override var height: Int,
    override var background: Color = Color(0F, 0F, 0F, 0F),
    override val parent: Widget? = null
): Widget {

    val children: MutableList<PositionedWidget> = mutableListOf()

    override var isHidden: Boolean = false

    fun add(child: PositionedWidget) {
        children += child
    }

    fun add(child: Widget) = add(PositionedWidget(0, 0, child))

    operator fun plusAssign(child: PositionedWidget) = add(child)
    operator fun plusAssign(child: Widget) = add(child)

    override fun render(renderers: Renderers, delta: Double) {
        super.render(renderers, delta)
        children.forEach {
            it.widget.render(renderers, delta)
        }
    }
}
