package net.environmentz.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Unique
    private static final int EASY_SPAWN_SEARCH_RADIUS = 2400;
    @Unique
    private static final int EASY_SPAWN_SEARCH_INTERVAL = 64;

    @ModifyExpressionValue(method = "setupSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/util/MultiNoiseUtil$MultiNoiseSampler;findBestSpawnPosition()Lnet/minecraft/util/math/BlockPos;"))
    private static BlockPos findEasySpawnPositionMixin(BlockPos original, @Local(argsOnly = true) ServerWorld world) {
        if (!ConfigInit.CONFIG.easyWorldSpawn) {
            return original;
        }
        BlockPos origin = original.withY(world.getSeaLevel());
        Pair<BlockPos, RegistryEntry<Biome>> result = world.locateBiome(biome -> biome.isIn(TagInit.EASY_SPAWN), origin, EASY_SPAWN_SEARCH_RADIUS, EASY_SPAWN_SEARCH_INTERVAL,
                world.getHeight());
        return result != null ? result.getFirst() : original;
    }
}
