package dev.tildejustin.randomskin.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor
    File getAssetDirectory();
}
