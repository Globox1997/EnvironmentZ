package net.environmentz.init;

import net.environmentz.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemInit {

    // Item Group
    public static final RegistryKey<ItemGroup> ENVIRONMENTZ_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("environmentz", "item_group"));

    public static final int COOLING_HEATING_VALUE = 120;
    // Item
    public static final Item HEATING_STONES_ITEM = register("heating_stones", new HeatingStones(new Item.Settings().maxDamage(COOLING_HEATING_VALUE)));
    public static final Item WOLF_PELT_ITEM = register("wolf_pelt", new WolfPeltItem(new Item.Settings()));
    public static final Item POLAR_BEAR_FUR_ITEM = register("polar_bear_fur", new PolarBearFurItem(new Item.Settings()));
    public static final Item ICE_PACK_ITEM = register("ice_pack", new IcePack(new Item.Settings().maxDamage(COOLING_HEATING_VALUE)));
    // Armor
    public static final ArmorMaterial WOLF_ARMOR_MATERIAL = new WolfArmorMaterial();
    public static final ArmorMaterial WANDERER_ARMOR_MATERIAL = new WandererArmorMaterial();
    public static final Item WOLF_HELMET = register("wolf_helmet", new ArmorItem(WOLF_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final Item WOLF_CHESTPLATE = register("wolf_chestplate", new ArmorItem(WOLF_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final Item WOLF_LEGGINGS = register("wolf_leggings", new ArmorItem(WOLF_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final Item WOLF_BOOTS = register("wolf_boots", new ArmorItem(WOLF_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()));

    public static final Item WANDERER_HELMET = register("wanderer_helmet", new WandererArmor(WANDERER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final Item WANDERER_CHESTPLATE = register("wanderer_chestplate", new WandererArmor(WANDERER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final Item WANDERER_LEGGINGS = register("wanderer_leggings", new WandererArmor(WANDERER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final Item WANDERER_BOOTS = register("wanderer_boots", new WandererArmor(WANDERER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings()));

    // Potion
    public static final Potion COLD_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.WARMING, 3600));
    public static final Potion LONG_COLD_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.WARMING, 9600));
    public static final Potion OVERHEATING_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.COOLING, 3600));
    public static final Potion LONG_OVERHEATING_RESISTANCE = new Potion(new StatusEffectInstance(EffectInit.COOLING, 9600));

    private static Item register(String id, Item item) {
        return register(new Identifier("environmentz", id), item);
    }

    private static Item register(Identifier id, Item item) {
        ItemGroupEvents.modifyEntriesEvent(ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, ENVIRONMENTZ_ITEM_GROUP,
                FabricItemGroup.builder().icon(() -> new ItemStack(WOLF_CHESTPLATE)).displayName(Text.translatable("item.environmentz.item_group")).build());
        // Potion
        Registry.register(Registries.POTION, "cold_resistance", COLD_RESISTANCE);
        Registry.register(Registries.POTION, "long_cold_resistance", LONG_COLD_RESISTANCE);
        Registry.register(Registries.POTION, "overheating_resistance", OVERHEATING_RESISTANCE);
        Registry.register(Registries.POTION, "long_overheating_resistance", LONG_OVERHEATING_RESISTANCE);

        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_HELMET, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_CHESTPLATE, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_LEGGINGS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(WOLF_BOOTS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
    }

}
