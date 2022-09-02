package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.environmentz.util.TemperatureAspects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {

    public BucketItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && world.getTime() % 40 == 0 && stack.isOf(Items.LAVA_BUCKET) && entity instanceof PlayerEntity
                && (((PlayerEntity) entity).getMainHandStack().equals(stack) || ((PlayerEntity) entity).getOffHandStack().equals(stack)))
            TemperatureAspects.heatPlayerWithItem((PlayerEntity) entity, 1);

        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
