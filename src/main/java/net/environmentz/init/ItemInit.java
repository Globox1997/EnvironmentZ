package net.environmentz.init;

import net.environmentz.item.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {
        // Item
        public static final HeatingStones HEATING_STONES = new HeatingStones(new Item.Settings().group(ItemGroup.MISC));
        public static final HeatedStones HEATED_STONES = new HeatedStones(
                        new Item.Settings().group(ItemGroup.MISC).maxDamage(100));
        public static final WolfPeltItem WOLF_PELT_ITEM = new WolfPeltItem(new Item.Settings().group(ItemGroup.MISC));
        public static final LeatherStrap LEATHER_STRAP_ITEM = new LeatherStrap(
                        new Item.Settings().group(ItemGroup.MISC));
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

        public static void init() {
                // Item
                Registry.register(Registry.ITEM, new Identifier("environmentz", "heating_stones"), HEATING_STONES);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "heated_stones"), HEATED_STONES);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_pelt"), WOLF_PELT_ITEM);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "leather_strap"), LEATHER_STRAP_ITEM);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "polar_bear_fur"), POLAR_BEAR_FUR_ITEM);
                // Armor
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_helmet"), WOLF_HELMET);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_chestplate"), WOLF_CHESTPLATE);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_leggings"), WOLF_LEGGINGS);
                Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_boots"), WOLF_BOOTS);
        }

}
