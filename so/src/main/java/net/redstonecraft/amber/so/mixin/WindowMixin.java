package net.redstonecraft.amber.so.mixin;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Window.class)
public class WindowMixin {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 2), index = 1)
    private int modifyGlVersionMajor(int hint) {
        return 4;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", ordinal = 3), index = 1)
    private int modifyGlVersionMinor(int hint) {
        return 6;
    }

}
