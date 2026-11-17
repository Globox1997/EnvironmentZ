package net.environmentz.item;

import net.environmentz.EnvironmentzMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

public class EnvironmentzArmorMaterials {

    public static final ArmorMaterial WANDERER = create("wanderer", 4, defense(1, 1, 2, 1, 2), 5);

    public static final ArmorMaterial WOLF = create("wolf", 7, defense(1, 2, 3, 2, 3), 15);

    public static final ArmorMaterial WOOL = create("wool", 4, defense(1, 1, 2, 1, 2), 5);

    private static ArmorMaterial create(String name, int durability, Map<ArmorType, Integer> defense, int enchantability) {
        TagKey<Item> repairIngredient = TagKey.create(Registries.ITEM, EnvironmentzMain.identifierOf("repairs_" + name + "_armor"));
        ResourceKey<EquipmentAsset> assetId = ResourceKey.create(EquipmentAssets.ROOT_ID, EnvironmentzMain.identifierOf(name));
        return new ArmorMaterial(durability, defense, enchantability, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, repairIngredient, assetId);
    }

    private static Map<ArmorType, Integer> defense(int boots, int leggings, int chestplate, int helmet, int body) {
        EnumMap<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, leggings);
        map.put(ArmorType.CHESTPLATE, chestplate);
        map.put(ArmorType.HELMET, helmet);
        map.put(ArmorType.BODY, body);
        return map;
    }

}
