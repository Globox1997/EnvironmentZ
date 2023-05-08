package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(SpawnLocating.class)
public class SpawnLocatingMixin {

    @ModifyVariable(method = "findOverworldSpawn", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/world/ServerWorld;getChunk(II)Lnet/minecraft/world/chunk/WorldChunk;", ordinal = 0))
    private static WorldChunk findOverworldSpawnMixin(WorldChunk original, ServerWorld world, int x, int z) {
        if (ConfigInit.CONFIG.easy_world_spawn) {
            for (int i = 0; i < 10; i++) {
                for (int u = 0; u < 10; u++) {
                    BlockPos newSpawnPos = new BlockPos(x + 160 * i, 100, z + 160 * u);
                    if (world.getBiome(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, newSpawnPos)).isIn(TagInit.EASY_SPAWN)) {
                        return world.getChunk(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
                    }
                }
            }
        }
        return original;
    }
}
