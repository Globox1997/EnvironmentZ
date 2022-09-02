package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.environmentz.util.TemperatureAspects;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.world.World;

@Mixin(WallStandingBlockItem.class)
public abstract class WallStandingBlockItemMixin extends BlockItem {

    public WallStandingBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && world.getTime() % 60 == 0 && entity instanceof PlayerEntity
                && (((PlayerEntity) entity).getMainHandStack().equals(stack) || ((PlayerEntity) entity).getOffHandStack().equals(stack)))
            TemperatureAspects.heatPlayerWithItem((PlayerEntity) entity, 1);

        super.inventoryTick(stack, world, entity, slot, selected);
    }

}
