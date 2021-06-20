package net.environmentz.init;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TagInit {
    // Item
    public static final Tag<Item> WARM_ARMOR = TagRegistry.item(new Identifier("environmentz", "warm_armor"));
    public static final Tag<Item> INSOLATING_ITEM = TagRegistry.item(new Identifier("environmentz", "insolating_item"));
    public static final Tag<Item> ALLOWED_ARMOR = TagRegistry.item(new Identifier("environmentz", "allowed_armor"));
    // Block
    public static final Tag<Block> WARMING_BLOCKS = TagRegistry.block(new Identifier("environmentz", "warming_blocks"));

    public static void init() {
    }

}
