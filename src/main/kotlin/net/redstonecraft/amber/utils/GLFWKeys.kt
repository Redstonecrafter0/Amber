package net.redstonecraft.amber.utils

import org.lwjgl.glfw.GLFW

object GLFWKeys {

    val keys = GLFW::class.java.declaredFields.filter { it.name.startsWith("GLFW_KEY_") }.associate { it.name.removePrefix("GLFW_KEY_") to it.getInt(null) } - "UNKNOWN" + ("NONE" to -1)
    val keyNames = keys.map { (k, v) -> v to k }.toMap()

    operator fun get(name: String?): Int? = if (name == null) null else keys[name.uppercase()]

    fun getName(key: Int?): String? = if (key == null) null else keyNames[key]

}
