package net.environmentz.init;

import java.util.Set;
import java.util.function.Function;

import net.environmentz.EnvironmentzMain;
import net.environmentz.block.CopperHeater;
import net.environmentz.block.entity.CopperHeaterEntity;
import net.environmentz.block.screen.CopperHeaterScreenHandler;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockInit {

    public static final Block COPPER_HEATER = register("copper_heater", CopperHeater::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK.weathering().unaffected()));

    public static final MenuType<CopperHeaterScreenHandler> COPPER_HEATER_SCREEN_HANDLER_TYPE = Registry.register(BuiltInRegistries.MENU, EnvironmentzMain.identifierOf("copper_heater"),
            new MenuType<>(CopperHeaterScreenHandler::new, FeatureFlags.VANILLA_SET));

    public static final BlockEntityType<CopperHeaterEntity> HEATER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, EnvironmentzMain.identifierOf("heater"),
            new BlockEntityType<>(CopperHeaterEntity::new, Set.of(COPPER_HEATER)));

    private static Block register(String id, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
        Identifier identifier = EnvironmentzMain.identifierOf(id);
        Block block = Registry.register(BuiltInRegistries.BLOCK, identifier, factory.apply(properties.setId(ResourceKey.create(Registries.BLOCK, identifier))));
        Item item = Registry.register(BuiltInRegistries.ITEM, identifier,
                new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, identifier)).useBlockDescriptionPrefix()));
        CreativeModeTabEvents.modifyOutputEvent(ItemInit.ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.accept(item));
        return block;
    }

    public static void init() {
    }

}
