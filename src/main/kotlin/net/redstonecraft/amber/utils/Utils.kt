package net.redstonecraft.amber.utils

import net.minecraft.client.MinecraftClient
import net.redstonecraft.opengl.camera.OrthographicCamera
import java.awt.Color
import kotlin.math.min
import kotlin.reflect.jvm.internal.impl.load.java.UtilsKt

object Utils {

    fun <T> paginate(list: List<T>, page: Int, perPage: Int): List<T> {
        return try {
            val start = page * perPage
            val end = start + perPage
            list.subList(start, min(end, list.size))
        } catch (_: IllegalArgumentException) {
            emptyList()
        } catch (_: IndexOutOfBoundsException) {
            emptyList()
        }
    }

}

fun gameOrthoCamTop() = OrthographicCamera(0F, MinecraftClient.getInstance().window.framebufferWidth.toFloat(), 0F, MinecraftClient.getInstance().window.framebufferHeight.toFloat())
fun gameOrthoCamBottom() = OrthographicCamera(0F, MinecraftClient.getInstance().window.framebufferWidth.toFloat(), MinecraftClient.getInstance().window.framebufferHeight.toFloat(), 0F)

fun resourceBytes(location: String) = UtilsKt::class.java.getResourceAsStream("/$location").readBytes()
fun resourceText(location: String) = UtilsKt::class.java.getResourceAsStream("/$location").readBytes().decodeToString()

fun Color.lighten(brightness: Float): Color {
    val hsb = Color.RGBtoHSB(red, green, blue, null)
    hsb[2] = hsb[2] + brightness
    if (hsb[2] > 1F) hsb[2] = 1F
    val rgb = Color.getHSBColor(hsb[0], hsb[1], hsb[2])
    return Color(rgb.red, rgb.green, rgb.blue, alpha)
}
