package net.environmentz.init;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.entry.ItemEntry;

public class LootInit {

    public static void init() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if ("minecraft:entities/wolf".equals(id.toString())) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(BinomialLootTableRange.create(2, 0.6F)).with(ItemEntry.builder(ItemInit.WOLF_PELT_ITEM));

                supplier.pool(poolBuilder);
            }
        });
    }

}
