package net.environmentz.init;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class LootInit {

    public static void init() {

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if ("minecraft:entities/wolf".equals(key.identifier().toString())) {
                LootPool pool = LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.WOLF_PELT_ITEM)).setRolls(ContextIntProviders.binomial(3, 0.6F)).build();
                tableBuilder.pool(pool);
            }
            if ("minecraft:entities/polar_bear".equals(key.identifier().toString())) {
                LootPool pool = LootPool.lootPool().add(LootItem.lootTableItem(ItemInit.POLAR_BEAR_FUR_ITEM)).setRolls(ContextIntProviders.binomial(2, 0.5F)).build();
                tableBuilder.pool(pool);
            }
        });
    }

}
