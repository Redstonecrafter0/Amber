package net.redstonecraft.amber.mixins;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import edu.rice.cs.util.ArgumentTokenizer;
import kotlin.Pair;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.redstonecraft.amber.commands.CommandManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public class MixinCommandSuggestor {

    @Shadow @Final TextFieldWidget textField;

    @Shadow private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Inject(method = "refresh", at = @At("TAIL"))
    private void injectRefresh(CallbackInfo ci) {
        Pair<List<String>, String> p = CommandManager.tabComplete(textField.getText());
        if (p != null) {
            List<String> list = p.getFirst();
            String cmd = p.getSecond();
            List<String> args = ArgumentTokenizer.tokenize(textField.getText().substring(textField.getText().indexOf(" ")));
            SuggestionsBuilder builder = new SuggestionsBuilder(textField.getText().substring(textField.getCursor()), Math.min(textField.getCursor(), getCloser(String.join(" ", args).lastIndexOf(" "), textField.getText().lastIndexOf(" "), textField.getCursor())));
            for (String i : args) {
                builder.suggest(i);
            }
            pendingSuggestions = builder.buildFuture();
        }
    }

    // get closer number of two numbers from number between them
    private int getCloser(int a, int b, int c) {
        if (Math.abs(a - c) < Math.abs(b - c)) {
            return a;
        } else {
            return b;
        }
    }

}
