package net.environmentz.init;

import java.util.Set;
import java.util.function.BiConsumer;

import net.environmentz.EnvironmentzMain;
import net.environmentz.block.CopperHeater;
import net.environmentz.block.entity.CopperHeaterEntity;
import net.environmentz.block.screen.CopperHeaterScreenHandler;
import net.environmentz.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockInit {

    private static final Identifier COPPER_HEATER_ID = EnvironmentzMain.identifierOf("copper_heater");

    public static Block COPPER_HEATER;

    public static MenuType<CopperHeaterScreenHandler> COPPER_HEATER_SCREEN_HANDLER_TYPE;

    public static BlockEntityType<CopperHeaterEntity> HEATER;

    public static void register(BiConsumer<Identifier, Block> registrar) {
        COPPER_HEATER = new CopperHeater(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK.weathering().unaffected()).setId(ResourceKey.create(Registries.BLOCK, COPPER_HEATER_ID)));
        registrar.accept(COPPER_HEATER_ID, COPPER_HEATER);
    }

    public static void registerItems(BiConsumer<Identifier, Item> registrar) {
        registrar.accept(COPPER_HEATER_ID, new BlockItem(COPPER_HEATER, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, COPPER_HEATER_ID)).useBlockDescriptionPrefix()));
    }

    public static void registerMenus(BiConsumer<Identifier, MenuType<?>> registrar) {
        COPPER_HEATER_SCREEN_HANDLER_TYPE = Services.registry().createMenuType(CopperHeaterScreenHandler::new);
        registrar.accept(COPPER_HEATER_ID, COPPER_HEATER_SCREEN_HANDLER_TYPE);
    }

    public static void registerBlockEntities(BiConsumer<Identifier, BlockEntityType<?>> registrar) {
        HEATER = new BlockEntityType<>(CopperHeaterEntity::new, Set.of(COPPER_HEATER));
        registrar.accept(EnvironmentzMain.identifierOf("heater"), HEATER);
    }

}
