package net.environmentz.fabric.platform;

import java.util.function.BiFunction;

import net.environmentz.platform.services.IRegistryHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(BiFunction<Integer, Inventory, T> factory) {
        return new MenuType<>(factory::apply, FeatureFlags.VANILLA_SET);
    }

}
