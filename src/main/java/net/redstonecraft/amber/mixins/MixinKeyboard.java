package net.redstonecraft.amber.mixins;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.redstonecraft.amber.events.EventManager;
import net.redstonecraft.amber.events.KeyboardKeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"))
    private void injectOnKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window == client.getWindow().getHandle()) {
            EventManager.fire(new KeyboardKeyEvent(key, scancode, action, modifiers));
        }
    }

}
