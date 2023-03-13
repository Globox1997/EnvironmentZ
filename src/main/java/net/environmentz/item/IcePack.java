package net.environmentz.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IcePack extends Item {

    public IcePack(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}
