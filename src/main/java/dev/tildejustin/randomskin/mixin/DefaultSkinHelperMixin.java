package dev.tildejustin.randomskin.mixin;

import dev.tildejustin.randomskin.RandomSkin;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
public abstract class DefaultSkinHelperMixin {
    @Overwrite
    public static Identifier getTexture(UUID uuid) {
        return RandomSkin.skin;
    }
}
