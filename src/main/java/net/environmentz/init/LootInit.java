package net.environmentz.init;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;

public class LootInit {

    public static void init() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if ("minecraft:entities/wolf".equals(id.toString())) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(BinomialLootNumberProvider.create(3, 0.6F)).with(ItemEntry.builder(ItemInit.WOLF_PELT_ITEM));
                supplier.pool(poolBuilder);
            }
            if ("minecraft:entities/polar_bear".equals(id.toString())) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(BinomialLootNumberProvider.create(2, 0.5F)).with(ItemEntry.builder(ItemInit.POLAR_BEAR_FUR_ITEM));
                supplier.pool(poolBuilder);
            }
        });
    }

}