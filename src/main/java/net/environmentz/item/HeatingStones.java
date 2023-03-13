package net.environmentz.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.world.World;

public class HeatingStones extends Item {

    public HeatingStones(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient && player.currentScreenHandler instanceof StonecutterScreenHandler)
            stack.setDamage(stack.getMaxDamage() - 1);

        super.onCraft(stack, world, player);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
