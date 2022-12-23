package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.util.TemperatureAspects;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {

    @Inject(method = "onSteppedOn", at = @At("HEAD"))
    private void onSteppedOnMixin(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo info) {
        if (!world.isClient && entity instanceof PlayerEntity)
            TemperatureAspects.heatPlayerWithBlock((PlayerEntity) entity, world, pos, 1, 1, false);
    }
}
