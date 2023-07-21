package net.environmentz.item;

import net.environmentz.init.ItemInit;
import net.minecraft.item.ArmorItem.Type;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WolfArmorMaterial implements ArmorMaterial {
    private static final int[] BASE_DURABILITY = new int[] { 11, 16, 15, 13 };
    private static final int[] PROTECTION_AMOUNTS = new int[] { 2, 3, 2, 1 };

    @Override
    public int getDurability(Type type) {
        return BASE_DURABILITY[type.ordinal()] * 7;
    }

    @Override
    public int getProtection(Type type) {
        return PROTECTION_AMOUNTS[type.ordinal()];
    }

    @Override
    public int getEnchantability() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ItemInit.WOLF_PELT_ITEM);
    }

    @Override
    public String getName() {
        return "wolf_pelt";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }

}