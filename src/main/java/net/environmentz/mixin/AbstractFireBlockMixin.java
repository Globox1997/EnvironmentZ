package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.ConfigInit;
import net.environmentz.util.TemperatureAspects;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin extends Block {

    public AbstractFireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void onEntityCollisionMixin(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo info) {
        if (!world.isClient && entity instanceof PlayerEntity)
            TemperatureAspects.heatPlayerWithBlock((PlayerEntity) entity, world, pos, 1, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient)
            TemperatureAspects.heatPlayerWithBlock(null, world, pos, ConfigInit.CONFIG.heating_up_block_range, ConfigInit.CONFIG.cold_protection_amount_addition);

        super.scheduledTick(state, world, pos, random);
    }

}
