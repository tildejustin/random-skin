package dev.tildejustin.randomskin.mixin;

import dev.tildejustin.randomskin.RandomSkin;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends Screen {
    @Unique
    ButtonWidget skinButton;

    @Inject(method = "init", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addSwapButton(CallbackInfo ci, int i) {
        skinButton = new ButtonWidget(201, this.width / 2 - 155 + 7 % 2 * 160, this.height / 6 + 24 * (7 >> 1), 150, 20, "Skin Mode: " + RandomSkin.mode);
        this.buttons.add(skinButton);
    }

    // need ordinal 0 because optifine calls id, making it run twice :P
    @Redirect(method = "buttonClicked", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;id:I", ordinal = 0))
    private int checkSkinButton(ButtonWidget instance) {
        if (instance.id == skinButton.id) {
            switch (RandomSkin.mode) {
                case random:
                    RandomSkin.mode = RandomSkin.Mode.cached;
                    break;
                case cached:
                    RandomSkin.mode = RandomSkin.Mode.random;
                    break;
            }
            this.skinButton.message = "Skin Mode: " + RandomSkin.mode;
            RandomSkin.saveProperties();
        }
        return instance.id;
    }
}
