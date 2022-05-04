package net.redstonecraft.amber.mixins;

import net.minecraft.client.render.GameRenderer;
import net.redstonecraft.amber.Amber;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 1))
    private void injectRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Vector2f size = new Vector2f(Amber.getBlurFb1().getWidth(), Amber.getBlurFb1().getHeight());
        Amber.getBlurFb1().bind();
        Amber.getHoriBlurRenderer().render(new Vector2f(0, 0), size, new Vector2f(0, 0), new Vector2f(1, 1));
        Amber.getHoriBlurRenderer().finish();
        Amber.getBlurFb2().bind();
        Amber.getVertBlurRenderer().render(new Vector2f(0, 0), size, new Vector2f(0, 0), new Vector2f(1, 1));
        Amber.getVertBlurRenderer().finish();
    }

}
