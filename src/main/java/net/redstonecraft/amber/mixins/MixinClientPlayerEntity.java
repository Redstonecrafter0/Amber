package net.redstonecraft.amber.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.redstonecraft.amber.events.EventManager;
import net.redstonecraft.amber.events.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectTick(CallbackInfo ci) {
        EventManager.fire(new TickEvent());
    }

}
