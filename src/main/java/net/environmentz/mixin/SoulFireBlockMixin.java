package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
@Mixin(SoulFireBlock.class)
public abstract class SoulFireBlockMixin extends AbstractFireBlock {

    public SoulFireBlockMixin(Settings settings, float damage) {
        super(settings, damage);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.createAndScheduleBlockTick(pos, this, getFireTickDelay(world.random));
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, this, getFireTickDelay(world.random));
    }

    private static int getFireTickDelay(Random random) {
        return 30 + random.nextInt(10);
    }

}
