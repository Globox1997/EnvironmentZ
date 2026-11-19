package net.environmentz.init;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class LootInit {

    public static void modifyLootTable(ResourceKey<LootTable> key, Consumer<LootPool> poolAdder) {
        if ("minecraft:entities/wolf".equals(key.identifier().toString())) {
            poolAdder.accept(LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.WOLF_PELT_ITEM)).setRolls(ContextIntProviders.binomial(3, 0.6F)).build());
        }
        if ("minecraft:entities/polar_bear".equals(key.identifier().toString())) {
            poolAdder.accept(LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.POLAR_BEAR_FUR_ITEM)).setRolls(ContextIntProviders.binomial(2, 0.5F)).build());
        }
    }

}
