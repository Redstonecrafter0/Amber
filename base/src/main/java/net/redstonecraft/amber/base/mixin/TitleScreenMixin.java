package net.redstonecraft.amber.base.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.redstonecraft.amber.AmberMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	@Inject(method = "init", at = @At("HEAD"))
	public void preInit(CallbackInfo ci) {
		AmberMod.INSTANCE.init();
	}

}
