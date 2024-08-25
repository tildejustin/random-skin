package dev.tildejustin.randomskin.mixin;

import dev.tildejustin.randomskin.RandomSkin;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
    @Inject(method = "init", at = @At(value = "HEAD"))
    private void resetSkin(CallbackInfo ci) {
        RandomSkin.texturesLoaded = false;
        RandomSkin.setNextSkin();
    }
}
