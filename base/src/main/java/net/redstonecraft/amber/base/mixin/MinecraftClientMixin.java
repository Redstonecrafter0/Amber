package net.redstonecraft.amber.base.mixin;

import net.minecraft.client.MinecraftClient;
import net.redstonecraft.amber.AmberMod;
import net.redstonecraft.amber.base.event.FrameEvent;
import net.redstonecraft.amber.base.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter;beginRenderTick(J)I"))
    public void preRender(boolean tick, CallbackInfo ci) {
        TickEvent event = new TickEvent();
        AmberMod.INSTANCE.getEventManager().fire(event);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;swapBuffers()V"))
    public void render(boolean tick, CallbackInfo ci) {
        FrameEvent event = new FrameEvent();
        AmberMod.INSTANCE.getEventManager().fire(event);
    }

}
