package net.redstonecraft.amber.mixins;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.math.MathHelper;
import net.redstonecraft.amber.commands.BaseCommand;
import net.redstonecraft.amber.commands.CommandManager;
import net.redstonecraft.amber.commands.CommandTools;
import net.redstonecraft.amber.modules.BaseModule;
import net.redstonecraft.amber.modules.Category;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CommandSuggestor.class)
public abstract class MixinCommandSuggestor {

    @Shadow @Nullable CommandSuggestor.@Nullable SuggestionWindow window;

    @Shadow @Final TextFieldWidget textField;

    @Shadow @Final TextRenderer textRenderer;

    @Shadow @Final boolean chatScreenSized;

    @Shadow @Final Screen owner;

    @Shadow protected abstract List<Suggestion> sortSuggestions(Suggestions suggestions);

    @Inject(method = "showSuggestions", at = @At("TAIL"))
    private void injectShowSuggestions(boolean narrateFirstSuggestion, CallbackInfo ci) {
        if (textField.getText().startsWith(".") && !textField.getText().startsWith("..") && owner instanceof ChatScreen chatScreen) {
            if (textField.getText().equals(".")) {
                SuggestionsBuilder builder = new SuggestionsBuilder(".", ".", 1);
                List<String> list = new ArrayList<>();
                CommandManager.INSTANCE.getCommands().keySet().forEach(list::addAll);
                for (String i : list) {
                    builder.suggest(i);
                }
                Suggestions suggestions = builder.build();
                suggest(chatScreen, suggestions);
            } else {
                Suggestions suggestions = CommandManager.tabComplete(textField.getText(), textField.getCursor());
                if (suggestions == null) return;
                suggest(chatScreen, suggestions);
            }
        }
    }

    private void suggest(ChatScreen chatScreen, Suggestions suggestions) {
        int w = 0;
        for (Suggestion suggestion : suggestions.getList()) {
            w = Math.max(w, textRenderer.getWidth(suggestion.getText()));
        }
        if (w == 0) return;
        int x = MathHelper.clamp(textField.getCharacterX(suggestions.getRange().getStart()), 0, textField.getCharacterX(0) + textField.getInnerWidth() - w);
        int y = chatScreenSized ? owner.height - 12 : 72;
        window = chatScreen.commandSuggestor.new SuggestionWindow(x, y, w, sortSuggestions(suggestions), false);
    }

}
