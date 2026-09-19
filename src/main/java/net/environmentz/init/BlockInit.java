package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.environmentz.block.CopperHeater;
import net.environmentz.block.entity.CopperHeaterEntity;
import net.environmentz.block.screen.CopperHeaterScreenHandler;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class BlockInit {

    public static final Block COPPER_HEATER = register("copper_heater", new CopperHeater(AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)));

    public static final ScreenHandlerType<CopperHeaterScreenHandler> COPPER_HEATER_SCREEN_HANDLER_TYPE = Registry.register(Registries.SCREEN_HANDLER, EnvironmentzMain.identifierOf("copper_heater"),
            new ScreenHandlerType<>(CopperHeaterScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    public static final BlockEntityType<CopperHeaterEntity> HEATER = Registry.register(Registries.BLOCK_ENTITY_TYPE, EnvironmentzMain.identifierOf("heater"), BlockEntityType.Builder.create(CopperHeaterEntity::new, COPPER_HEATER).build(null));

    private static Block register(String id, Block block) {
        return register(EnvironmentzMain.identifierOf(id), block);
    }

    private static Block register(Identifier id, Block block) {
        Item item = Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(ItemInit.ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.add(item));

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void init() {
    }

}
