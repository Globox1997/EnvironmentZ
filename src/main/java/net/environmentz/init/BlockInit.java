package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.environmentz.block.CopperHeater;
import net.environmentz.block.entity.CopperHeaterEntity;
import net.environmentz.block.screen.CopperHeaterScreenHandler;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockInit {

    public static final Block COPPER_HEATER = register("copper_heater", new CopperHeater(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final MenuType<CopperHeaterScreenHandler> COPPER_HEATER_SCREEN_HANDLER_TYPE = Registry.register(BuiltInRegistries.MENU, EnvironmentzMain.identifierOf("copper_heater"),
            new MenuType<>(CopperHeaterScreenHandler::new, FeatureFlags.VANILLA_SET));

    public static final BlockEntityType<CopperHeaterEntity> HEATER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, EnvironmentzMain.identifierOf("heater"), BlockEntityType.Builder.of(CopperHeaterEntity::new, COPPER_HEATER).build(null));

    private static Block register(String id, Block block) {
        return register(EnvironmentzMain.identifierOf(id), block);
    }

    private static Block register(ResourceLocation id, Block block) {
        Item item = Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
        ItemGroupEvents.modifyEntriesEvent(ItemInit.ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.accept(item));

        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static void init() {
    }

}
