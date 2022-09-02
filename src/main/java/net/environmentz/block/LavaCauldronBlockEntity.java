package net.environmentz.block;

import net.environmentz.init.BlockInit;
import net.environmentz.init.ConfigInit;
import net.environmentz.util.TemperatureAspects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LavaCauldronBlockEntity extends BlockEntity {

    public LavaCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockInit.LAVA_CAULDRON_BLOCK_ENTITY, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, LavaCauldronBlockEntity blockEntity) {
        if (world.getTime() % 20 == 0)
            TemperatureAspects.heatPlayerWithBlock(null, world, pos, ConfigInit.CONFIG.heating_up_block_range, ConfigInit.CONFIG.cold_protection_amount_addition);
    }

}
