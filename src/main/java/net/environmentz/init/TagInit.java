package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class TagInit {
    // Item
    public static final TagKey<Item> WARM_ARMOR = TagKey.of(RegistryKeys.ITEM, EnvironmentzMain.identifierOf("warm_armor"));
    public static final TagKey<Item> INSOLATING_ITEM = TagKey.of(RegistryKeys.ITEM, EnvironmentzMain.identifierOf("insolating_item"));
    public static final TagKey<Item> NON_AFFECTING_ARMOR = TagKey.of(RegistryKeys.ITEM, EnvironmentzMain.identifierOf("non_affecting_armor"));
    public static final TagKey<Item> ARMOR_ITEMS = TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "enchantable/armor"));
    public static final TagKey<Item> ICE_ITEMS = TagKey.of(RegistryKeys.ITEM, EnvironmentzMain.identifierOf("ice_items"));
    public static final TagKey<Item> SLIM_ARMOR = TagKey.of(RegistryKeys.ITEM, EnvironmentzMain.identifierOf("slim_armor"));
    // Block
    // Biome
    public static final TagKey<Biome> EASY_SPAWN = TagKey.of(RegistryKeys.BIOME, EnvironmentzMain.identifierOf("easy_spawn_biomes"));

    public static void init() {
    }

}
