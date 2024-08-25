package dev.tildejustin.randomskin.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import dev.tildejustin.randomskin.RandomSkin;
import dev.tildejustin.randomskin.mixin.accessor.PlayerListEntryAccessor;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow
    private Identifier getSkinTexture;

    @Shadow
    private String model;

    @Shadow
    private boolean texturesLoaded;

    @Shadow
    public abstract GameProfile getProfile();

    @Redirect(method = "getSkinTexture", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/PlayerListEntry;getSkinTexture:Lnet/minecraft/util/Identifier;"))
    private Identifier noSkinTexture(PlayerListEntry instance) {
        if (this.getProfile().getId() == RandomSkin.client.player.getUuid() && !(RandomSkin.client.currentScreen instanceof ConnectScreen)) {
            return null;
        }
        return ((PlayerListEntryAccessor) instance).getGetSkinTexture();
    }

    @Inject(method = "loadTextures", at = @At(value = "HEAD"), cancellable = true)
    protected void injectLoadTextures(CallbackInfo ci) {
        if (this.getProfile().getId() == RandomSkin.client.player.getUuid() && !(RandomSkin.client.currentScreen instanceof ConnectScreen)) {
            synchronized (this) {
                if (!RandomSkin.texturesLoaded) {
                    RandomSkin.texturesLoaded = this.texturesLoaded = true;
                    this.getSkinTexture = RandomSkin.skin;
                    this.model = RandomSkin.model;
                }
            }
            ci.cancel();
        }
    }

    @Mixin(targets = {"net.minecraft.client.network.PlayerListEntry$1"})
    private abstract static class class_1890$1Mixin implements PlayerSkinProvider.class_1890 {
        @Inject(method = "method_7047", at = @At(value = "HEAD"))
        private void getTextureUrl(MinecraftProfileTexture.Type type, Identifier identifier, MinecraftProfileTexture minecraftProfileTexture, CallbackInfo ci) {
            System.out.println(minecraftProfileTexture.getUrl());
        }
    }
}
