package net.environmentz.init;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagInit {
    // Item
    public static final TagKey<Item> WARM_ARMOR = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "warm_armor"));
    public static final TagKey<Item> INSOLATING_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "insolating_item"));
    public static final TagKey<Item> ALLOWED_ARMOR = TagKey.of(Registry.ITEM_KEY, new Identifier("environmentz", "allowed_armor"));
    // Block

    public static void init() {
    }

}
