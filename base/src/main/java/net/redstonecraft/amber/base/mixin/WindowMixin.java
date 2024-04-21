package net.redstonecraft.amber.base.mixin;

import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Window.class)
public class WindowMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 1), index = 1)
    private int modifyGlContextHint(int hint) {
        return GLFW.GLFW_EGL_CONTEXT_API; // TODO: allow Windows or first test if EGL runs on Windows
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 2), index = 1)
    private int modifyGlVersionMajor(int hint) {
        return 4;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 3), index = 1)
    private int modifyGlVersionMinor(int hint) {
        return 5;
    }

}
