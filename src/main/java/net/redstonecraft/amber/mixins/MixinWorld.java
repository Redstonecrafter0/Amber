package net.redstonecraft.amber.mixins;

import net.minecraft.world.World;
import net.redstonecraft.amber.modules.modules.world.EnvironmentModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "getRainGradient", at = @At("RETURN"), cancellable = true)
    private void injectGetRainGradient(float delta, CallbackInfoReturnable<Float> cir) {
        if (EnvironmentModule.INSTANCE.getHideRain()) {
            cir.setReturnValue(0F);
        }
    }

}
