package net.environmentz.item;

import net.environmentz.init.ItemInit;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WandererArmorMaterial implements ArmorMaterial {
    private static final int[] BASE_DURABILITY = new int[] { 10, 12, 13, 8 };
    private static final int[] PROTECTION_AMOUNTS = new int[] { 1, 1, 2, 1 };

    @Override
    public int getDurability(EquipmentSlot equipmentSlot) {
        return BASE_DURABILITY[equipmentSlot.getEntitySlotId()] * 7;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot equipmentSlot) {
        return PROTECTION_AMOUNTS[equipmentSlot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return 5;
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
        return "wanderer";
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
