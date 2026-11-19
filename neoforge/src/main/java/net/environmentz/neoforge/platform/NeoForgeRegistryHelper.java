package net.environmentz.neoforge.platform;

import java.util.function.BiFunction;

import net.environmentz.platform.services.IRegistryHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(BiFunction<Integer, Inventory, T> factory) {
        return IMenuTypeExtension.create((containerId, inventory, buffer) -> factory.apply(containerId, inventory));
    }

}
