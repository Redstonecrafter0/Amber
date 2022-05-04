package net.redstonecraft.amber.renderer.backgrounds

import net.redstonecraft.amber.renderer.Renderers
import net.redstonecraft.opengl.render.Texture
import org.joml.Vector2f
import java.awt.Color

interface Background {
    fun render(renderers: Renderers, delta: Double, x: Float, y: Float, w: Float, h: Float)
}

class ColorBackground(var color: Color) : Background {

    override fun render(renderers: Renderers, delta: Double, x: Float, y: Float, w: Float, h: Float) {
        renderers.nvg.render {
            fill(rgba(color.red, color.green, color.blue, color.alpha)) {
                rect(x, y, w, h)
            }
        }
    }
}

class TextureBackground(var texture: Texture, var texPos: Vector2f = Vector2f(0F, 0F), var texSize: Vector2f = Vector2f(1F, 1F)) : Background {

    override fun render(renderers: Renderers, delta: Double, x: Float, y: Float, w: Float, h: Float) {
        renderers.texture.render(texture, Vector2f(x, y), Vector2f(w, h), texPos = texPos, texSize = texSize)
    }
}
