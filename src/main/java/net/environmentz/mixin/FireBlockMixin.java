package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.ConfigInit;
import net.environmentz.util.TemperatureAspects;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "scheduledTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/world/ServerWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 0))
    private void scheduledTickMixin(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        if (!world.isClient)
            TemperatureAspects.heatPlayerWithBlock(null, world, pos, ConfigInit.CONFIG.heating_up_block_range, ConfigInit.CONFIG.cold_protection_amount_addition, true);
    }
}
