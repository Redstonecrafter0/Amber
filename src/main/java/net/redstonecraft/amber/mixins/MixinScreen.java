package net.redstonecraft.amber.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.redstonecraft.amber.commands.CommandManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow public abstract void sendMessage(String message, boolean toHud);

    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method = "sendMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private void injectSendMessage(String message, CallbackInfo ci) {
        if (message.startsWith(".")) {
            if (message.startsWith("..")) {
                sendMessage(message.substring(2), true);
            } else {
                CommandManager.INSTANCE.dispatch(message.substring(1));
            }
            if (client != null) {
                client.inGameHud.getChatHud().addToMessageHistory(message);
            }
            ci.cancel();
        }
    }

}
