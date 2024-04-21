package net.redstonecraft.amber

import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.redstonecraft.amber.base.config.AmberConfigManager
import net.redstonecraft.amber.base.event.AmberEventManager
import net.redstonecraft.amber.base.module.AmberModuleManager
import org.lwjgl.PointerBuffer
import org.lwjgl.egl.EGL
import org.lwjgl.egl.EGL10
import org.lwjgl.egl.EGL15.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWNativeEGL
import org.lwjgl.opengl.EXTEGLImageStorage
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL45.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object AmberMod : ModInitializer {

    lateinit var dir: File
        private set

    const val mod = "amber"

    val logger: Logger = LoggerFactory.getLogger("Amber")

    val eventManager = AmberEventManager()
    lateinit var moduleManager: AmberModuleManager
        private set
    lateinit var configManager: AmberConfigManager
        private set

    private var initialized = false

    override fun onInitialize() {
    }

    fun init() {
        if (!initialized) {
            dir = MinecraftClient.getInstance().runDirectory.resolve("amber").also { it.mkdirs() }
            moduleManager = AmberModuleManager()
            configManager = AmberConfigManager()
            try {
                val extraLoader = Class.forName("net.redstonecraft.amber.extra.Loader")
                extraLoader.getMethod("init").invoke(extraLoader.kotlin.objectInstance)
            } catch (_: ClassNotFoundException) {
                // only amber base present
            }

            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
            val off = GLFW.glfwCreateWindow(MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight, "", 0, 0)
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE)
            val egl = GLFWNativeEGL.glfwGetEGLContext(off)
            val eglDisplay = GLFWNativeEGL.glfwGetEGLDisplay()
            val major = intArrayOf(0)
            val minor = intArrayOf(0)
            eglInitialize(eglDisplay, major, minor)
            println("${major[0]}.${minor[0]}")
            EGL.createDisplayCapabilities(eglDisplay, major[0], minor[0])
            GLFW.glfwMakeContextCurrent(off)
            GL.createCapabilities()

            val fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)
            val texture = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, texture)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, 0)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0)

            val img = eglCreateImage(eglDisplay, egl, EGL_GL_TEXTURE_2D, texture.toLong(), PointerBuffer.allocateDirect(0))

            GLFW.glfwMakeContextCurrent(MinecraftClient.getInstance().window.handle)

            val texture1 = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, texture)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, MinecraftClient.getInstance().window.framebufferWidth, MinecraftClient.getInstance().window.framebufferHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, 0)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            EXTEGLImageStorage.glEGLImageTargetTexStorageEXT(GL_TEXTURE_2D, img, intArrayOf(EGL_NONE))

            initialized = true
        }
    }

}
