package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.ConfigInit;
import net.environmentz.util.TemperatureAspects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;burnTime:I", ordinal = 0))
    private static void tickMixin(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo info) {
        if (world.getTime() % 20 == 0)
            TemperatureAspects.heatPlayerWithBlock(null, world, pos, ConfigInit.CONFIG.heating_up_block_range, ConfigInit.CONFIG.cold_protection_amount_addition, false);
    }
}
