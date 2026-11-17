package net.environmentz.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Unique
    private static final int EASY_SPAWN_SEARCH_RADIUS = 2400;
    @Unique
    private static final int EASY_SPAWN_SEARCH_INTERVAL = 64;

    @ModifyExpressionValue(method = "setInitialSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Climate$Sampler;findSpawnPosition()Lnet/minecraft/core/BlockPos;"))
    private static BlockPos findEasySpawnPositionMixin(BlockPos original, @Local(argsOnly = true) ServerLevel world) {
        if (!ConfigInit.CONFIG.easyWorldSpawn) {
            return original;
        }
        BlockPos origin = original.atY(world.getSeaLevel());
        Pair<BlockPos, Holder<Biome>> result = world.findClosestBiome3d(biome -> biome.is(TagInit.EASY_SPAWN), origin, EASY_SPAWN_SEARCH_RADIUS, EASY_SPAWN_SEARCH_INTERVAL,
                world.getHeight());
        return result != null ? result.getFirst() : original;
    }
}
