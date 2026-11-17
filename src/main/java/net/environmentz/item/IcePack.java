package net.environmentz.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IcePack extends Item {

    public IcePack(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
