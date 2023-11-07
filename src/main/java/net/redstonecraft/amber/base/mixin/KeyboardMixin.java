package net.redstonecraft.amber.base.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.redstonecraft.amber.AmberMod;
import net.redstonecraft.amber.base.event.KeyPressEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void preOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window == client.getWindow().getHandle()) {
            KeyPressEvent event = new KeyPressEvent(key, scancode, action, modifiers);
            AmberMod.INSTANCE.getEventManager().fire(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

}
