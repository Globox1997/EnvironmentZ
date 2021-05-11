package net.environmentz.init;

import net.environmentz.item.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {
        // Item
        public static final HeatingStones HEATING_STONES = new HeatingStones(new Item.Settings().group(ItemGroup.MISC));
        public static final HeatedStones HEATED_STONES = new HeatedStones(
                        new Item.Settings().group(ItemGroup.MISC).maxDamage(100));
        public static final WolfPeltItem WOLF_PELT_ITEM = new WolfPeltItem(new Item.Settings().group(ItemGroup.MISC));
        public static final PolarBearFurItem POLAR_BEAR_FUR_ITEM = new PolarBearFurItem(
                        new Item.Settings().group(ItemGroup.MISC));
        // Armor
        public static final ArmorMaterial WOLF_ARMOR_MATERIAL = new WolfArmorMaterial();
        public static final Item WOLF_HELMET = new WolfArmor(WOLF_ARMOR_MATERIAL, EquipmentSlot.HEAD,
                        new Item.Settings().group(ItemGroup.COMBAT));
        public static final Item WOLF_CHESTPLATE = new WolfArmor(WOLF_ARMOR_MATERIAL, EquipmentSlot.CHEST,
                        new Item.Settings().group(ItemGroup.COMBAT));
        public static final Item WOLF_LEGGINGS = new WolfArmor(WOLF_ARMOR_MATERIAL, EquipmentSlot.LEGS,
                        new Item.Settings().group(ItemGroup.COMBAT));
        public static final Item WOLF_BOOTS = new WolfArmor(WOLF_ARMOR_MATERIAL, EquipmentSlot.FEET,
                        new Item.Settings().group(ItemGroup.COMBAT));
        // Potion
        public static final Potion COLD_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.WARMING, 3600));
        public static final Potion LONG_COLD_RESISTANCE = new Potion(
                        new StatusEffectInstance(EffectInit.WARMING, 9600));
        public static final Potion OVERHEATING_RESISTANCE = new Potion(
                        new StatusEffectInstance(EffectInit.COOLING, 3600));
        public static final Potion LONG_OVERHEATING_RESISTANCE = new Potion(
                        new StatusEffectInstance(EffectInit.COOLING, 9600));

        public static void init() {
                // Item
                Registry.register(Registry.ITEM, new Identifier("environmentz", "heating_stones"), HEATING_STONES);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "heated_stones"), HEATED_STONES);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_pelt"), WOLF_PELT_ITEM);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "polar_bear_fur"), POLAR_BEAR_FUR_ITEM);
                // Armor
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_helmet"), WOLF_HELMET);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_chestplate"), WOLF_CHESTPLATE);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_leggings"), WOLF_LEGGINGS);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_boots"), WOLF_BOOTS);
                // Potion
                Registry.register(Registry.POTION, "cold_resistance", COLD_RESISTANCE);
                Registry.register(Registry.POTION, "long_cold_resistance", LONG_COLD_RESISTANCE);
                Registry.register(Registry.POTION, "overheating_resistance", OVERHEATING_RESISTANCE);
                Registry.register(Registry.POTION, "long_overheating_resistance", LONG_OVERHEATING_RESISTANCE);
        }

}
