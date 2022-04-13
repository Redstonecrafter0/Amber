package net.redstonecraft.amber.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.redstonecraft.amber.events.EventManager;
import net.redstonecraft.amber.events.MousePressEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void injectOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == client.getWindow().getHandle()) {
            EventManager.fire(new MousePressEvent(button, action, mods));
        }
    }

}
