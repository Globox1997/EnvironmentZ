package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

public class TagInit {
    // Item
    public static final TagKey<Item> WARM_ARMOR = TagKey.create(Registries.ITEM, EnvironmentzMain.identifierOf("warm_armor"));
    public static final TagKey<Item> INSOLATING_ITEM = TagKey.create(Registries.ITEM, EnvironmentzMain.identifierOf("insolating_item"));
    public static final TagKey<Item> NON_AFFECTING_ARMOR = TagKey.create(Registries.ITEM, EnvironmentzMain.identifierOf("non_affecting_armor"));
    public static final TagKey<Item> ARMOR_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "enchantable/armor"));
    public static final TagKey<Item> ICE_ITEMS = TagKey.create(Registries.ITEM, EnvironmentzMain.identifierOf("ice_items"));
    public static final TagKey<Item> SLIM_ARMOR = TagKey.create(Registries.ITEM, EnvironmentzMain.identifierOf("slim_armor"));
    // Block
    // Biome
    public static final TagKey<Biome> EASY_SPAWN = TagKey.create(Registries.BIOME, EnvironmentzMain.identifierOf("easy_spawn_biomes"));

    public static void init() {
    }

}
