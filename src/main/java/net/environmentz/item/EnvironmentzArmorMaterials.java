package net.environmentz.item;

import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ItemInit;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class EnvironmentzArmorMaterials {

    public static final RegistryEntry<ArmorMaterial> WANDERER = register(EnvironmentzMain.identifierOf("wanderer"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 1);
        map.put(ArmorItem.Type.CHESTPLATE, 2);
        map.put(ArmorItem.Type.HELMET, 1);
        map.put(ArmorItem.Type.BODY, 2);
    }), 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(Items.PAPER), true);

    public static final RegistryEntry<ArmorMaterial> WOLF = register(EnvironmentzMain.identifierOf("wolf"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 2);
        map.put(ArmorItem.Type.CHESTPLATE, 3);
        map.put(ArmorItem.Type.HELMET, 2);
        map.put(ArmorItem.Type.BODY, 3);
    }), 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.ofItems(ItemInit.WOLF_PELT_ITEM), false);

    public static final RegistryEntry<ArmorMaterial> WOOL = register(EnvironmentzMain.identifierOf("wool"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 1);
        map.put(ArmorItem.Type.CHESTPLATE, 2);
        map.put(ArmorItem.Type.HELMET, 1);
        map.put(ArmorItem.Type.BODY, 2);
    }), 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.fromTag(ItemTags.WOOL), true);

    private static RegistryEntry<ArmorMaterial> register(Identifier id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, RegistryEntry<SoundEvent> equipSound, float toughness,
                                                         float knockbackResistance, Supplier<Ingredient> repairIngredient, boolean dyeable) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(id, "", dyeable));

        EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            enumMap.put(type, defense.get(type));
        }
        return Registry.registerReference(Registries.ARMOR_MATERIAL, id,
                new ArmorMaterial(enumMap, enchantability, equipSound, repairIngredient, list, toughness, knockbackResistance));
    }

}
