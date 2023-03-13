package net.redstonecraft.amber.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.redstonecraft.amber.events.EventManager;
import net.redstonecraft.amber.events.WindowResizeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class MixinWindow {

    @Inject(method = "onWindowSizeChanged", at = @At("TAIL"))
    private void injectOnWindowSizeChanged(long window, int width, int height, CallbackInfo ci) {
        if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
            EventManager.fire(new WindowResizeEvent(width, height));
        }
    }

}
