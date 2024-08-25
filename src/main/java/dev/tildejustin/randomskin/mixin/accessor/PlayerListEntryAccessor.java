package dev.tildejustin.randomskin.mixin.accessor;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryAccessor {
    @Accessor
    Identifier getGetSkinTexture();
}
