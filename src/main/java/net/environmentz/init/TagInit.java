package net.environmentz.init;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class TagInit {
    // Item
    public static final TagKey<Item> WARM_ARMOR = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "warm_armor"));
    public static final TagKey<Item> INSOLATING_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "insolating_item"));
    public static final TagKey<Item> NON_AFFECTING_ARMOR = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "non_affecting_armor"));
    public static final TagKey<Item> ARMOR_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "armor"));
    public static final TagKey<Item> ICE_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "ice_items"));
    public static final TagKey<Item> SLIM_ARMOR = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "slim_armor"));
    // Block
    // Biome
    public static final TagKey<Biome> EASY_SPAWN = TagKey.of(Registry.BIOME_KEY, new Identifier("environmentz", "easy_spawn_biomes"));

    public static final boolean isAutoTagLoaded = FabricLoader.getInstance().isModLoaded("autotag");

    public static void init() {
    }

}
