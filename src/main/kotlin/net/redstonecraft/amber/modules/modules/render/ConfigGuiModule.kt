package net.redstonecraft.amber.modules.modules.render

import net.redstonecraft.amber.modules.ToggleModule
import net.redstonecraft.amber.modules.modules.RenderCategory
import org.lwjgl.glfw.GLFW

object ConfigGuiModule:
    ToggleModule(
        "ConfigGUI",
        "A GUI to configure everything.",
        RenderCategory,
        preventEnableOnLoad = true,
        key = GLFW.GLFW_KEY_RIGHT_SHIFT
    ) {

}
