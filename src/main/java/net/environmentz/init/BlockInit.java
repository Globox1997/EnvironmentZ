package net.environmentz.init;

import net.environmentz.block.LavaCauldronBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockInit {

    public static BlockEntityType<LavaCauldronBlockEntity> LAVA_CAULDRON_BLOCK_ENTITY;

    public static void init() {
        LAVA_CAULDRON_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "environmentz:laval_cauldron_block_entity",
                FabricBlockEntityTypeBuilder.create(LavaCauldronBlockEntity::new, Blocks.LAVA_CAULDRON).build(null));
    }

}
