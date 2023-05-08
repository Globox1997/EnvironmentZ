package net.environmentz.init;

import net.environmentz.item.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemInit {
    public static final int COOLING_HEATING_VALUE = 120;
    // Item
    public static final HeatingStones HEATING_STONES_ITEM = new HeatingStones(new Item.Settings().group(ItemGroup.MISC).maxDamage(COOLING_HEATING_VALUE));
    public static final WolfPeltItem WOLF_PELT_ITEM = new WolfPeltItem(new Item.Settings().group(ItemGroup.MISC));
    public static final PolarBearFurItem POLAR_BEAR_FUR_ITEM = new PolarBearFurItem(new Item.Settings().group(ItemGroup.MISC));
    public static final IcePack ICE_PACK_ITEM = new IcePack(new Item.Settings().group(ItemGroup.MISC).maxDamage(COOLING_HEATING_VALUE));
    // Armor
    public static final ArmorMaterial WOLF_ARMOR_MATERIAL = new WolfArmorMaterial();
    public static final ArmorMaterial WANDERER_ARMOR_MATERIAL = new WandererArmorMaterial();
    public static final Item WOLF_HELMET = new ArmorItem(WOLF_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item WOLF_CHESTPLATE = new ArmorItem(WOLF_ARMOR_MATERIAL, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item WOLF_LEGGINGS = new ArmorItem(WOLF_ARMOR_MATERIAL, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item WOLF_BOOTS = new ArmorItem(WOLF_ARMOR_MATERIAL, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));

    public static final Item WANDERER_HELMET = new WandererArmor(WANDERER_ARMOR_MATERIAL, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item WANDERER_CHESTPLATE = new WandererArmor(WANDERER_ARMOR_MATERIAL, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item WANDERER_LEGGINGS = new WandererArmor(WANDERER_ARMOR_MATERIAL, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
    public static final Item WANDERER_BOOTS = new WandererArmor(WANDERER_ARMOR_MATERIAL, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));

    // Potion
    public static final Potion COLD_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.WARMING, 3600));
    public static final Potion LONG_COLD_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.WARMING, 9600));
    public static final Potion OVERHEATING_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.COOLING, 3600));
    public static final Potion LONG_OVERHEATING_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.COOLING, 9600));

    public static void init() {
        // Item
        Registry.register(Registry.ITEM, new Identifier("environmentz", "heating_stones"), HEATING_STONES_ITEM);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_pelt"), WOLF_PELT_ITEM);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "polar_bear_fur"), POLAR_BEAR_FUR_ITEM);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "ice_pack"), ICE_PACK_ITEM);
        // Armor
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_helmet"), WOLF_HELMET);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_chestplate"), WOLF_CHESTPLATE);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_leggings"), WOLF_LEGGINGS);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wolf_boots"), WOLF_BOOTS);

        Registry.register(Registry.ITEM, new Identifier("environmentz", "wanderer_helmet"), WANDERER_HELMET);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wanderer_chestplate"), WANDERER_CHESTPLATE);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wanderer_leggings"), WANDERER_LEGGINGS);
        Registry.register(Registry.ITEM, new Identifier("environmentz", "wanderer_boots"), WANDERER_BOOTS);
        // Potion
        Registry.register(Registry.POTION, "cold_resistance", COLD_RESISTANCE);
        Registry.register(Registry.POTION, "long_cold_resistance", LONG_COLD_RESISTANCE);
        Registry.register(Registry.POTION, "overheating_resistance", OVERHEATING_RESISTANCE);
        Registry.register(Registry.POTION, "long_overheating_resistance", LONG_OVERHEATING_RESISTANCE);

        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_HELMET, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_CHESTPLATE, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_LEGGINGS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_BOOTS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
    }

}
