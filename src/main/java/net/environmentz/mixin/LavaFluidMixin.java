package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.ConfigInit;
import net.environmentz.util.TemperatureAspects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
    private boolean onlyOneTickAtATime = false;

    @Inject(method = "onRandomTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I", ordinal = 0))
    private void onRandomTickMixin(World world, BlockPos pos, FluidState state, Random random, CallbackInfo info) {
        if (!world.isClient && world.getTime() % 20 == 0)
            if (onlyOneTickAtATime) {
                TemperatureAspects.heatPlayerWithBlock(null, world, pos, ConfigInit.CONFIG.heating_up_block_range, ConfigInit.CONFIG.cold_protection_amount_addition, false);
                onlyOneTickAtATime = false;
            } else
                onlyOneTickAtATime = true;
    }
}
