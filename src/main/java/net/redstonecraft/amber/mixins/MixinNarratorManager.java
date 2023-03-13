package net.redstonecraft.amber.mixins;

import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.util.NarratorManager;
import net.redstonecraft.amber.modules.modules.misc.NarratorDisablerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NarratorManager.class)
public class MixinNarratorManager {

    @Inject(method = "getNarratorOption", at = @At("RETURN"), cancellable = true)
    private static void injectGetNarratorOption(CallbackInfoReturnable<NarratorMode> cir) {
        if (NarratorDisablerModule.INSTANCE.isEnabled()) {
            cir.setReturnValue(NarratorMode.OFF);
        }
    }
}
