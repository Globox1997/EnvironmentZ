package net.environmentz.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HeatingStones extends Item {

    public HeatingStones(Item.Properties settings) {
        super(settings);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        if (!player.level().isClientSide() && player.containerMenu instanceof StonecutterMenu) {
            stack.setDamageValue(stack.getMaxDamage() - 1);
        }
        super.onCraftedBy(stack, player);
    }

}
