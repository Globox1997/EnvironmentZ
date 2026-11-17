package net.environmentz.item;

import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ItemInit;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class EnvironmentzArmorMaterials {

    public static final Holder<ArmorMaterial> WANDERER = register(EnvironmentzMain.identifierOf("wanderer"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 1);
        map.put(ArmorItem.Type.CHESTPLATE, 2);
        map.put(ArmorItem.Type.HELMET, 1);
        map.put(ArmorItem.Type.BODY, 2);
    }), 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(Items.PAPER), true);

    public static final Holder<ArmorMaterial> WOLF = register(EnvironmentzMain.identifierOf("wolf"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 2);
        map.put(ArmorItem.Type.CHESTPLATE, 3);
        map.put(ArmorItem.Type.HELMET, 2);
        map.put(ArmorItem.Type.BODY, 3);
    }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(ItemInit.WOLF_PELT_ITEM), false);

    public static final Holder<ArmorMaterial> WOOL = register(EnvironmentzMain.identifierOf("wool"), Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
        map.put(ArmorItem.Type.BOOTS, 1);
        map.put(ArmorItem.Type.LEGGINGS, 1);
        map.put(ArmorItem.Type.CHESTPLATE, 2);
        map.put(ArmorItem.Type.HELMET, 1);
        map.put(ArmorItem.Type.BODY, 2);
    }), 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(ItemTags.WOOL), true);

    private static Holder<ArmorMaterial> register(ResourceLocation id, EnumMap<ArmorItem.Type, Integer> defense, int enchantability, Holder<SoundEvent> equipSound, float toughness,
                                                         float knockbackResistance, Supplier<Ingredient> repairIngredient, boolean dyeable) {
        List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(id, "", dyeable));

        EnumMap<ArmorItem.Type, Integer> enumMap = new EnumMap<ArmorItem.Type, Integer>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            enumMap.put(type, defense.get(type));
        }
        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, id,
                new ArmorMaterial(enumMap, enchantability, equipSound, repairIngredient, list, toughness, knockbackResistance));
    }

}
