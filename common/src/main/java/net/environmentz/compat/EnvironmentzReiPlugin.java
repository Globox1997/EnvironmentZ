package net.environmentz.compat;

import java.util.List;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.anvil.AnvilRecipe;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnvironmentzReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (Holder<Item> registryEntry : BuiltInRegistries.ITEM.getTagOrEmpty(TagInit.ARMOR_ITEMS)) {
            addRecipe(registry, registryEntry.value());
        }
    }

    private static void addRecipe(DisplayRegistry registry, Item item) {
        ItemStack itemStack = new ItemStack(item);
        Identifier itemIdentifier = BuiltInRegistries.ITEM.getKey(item);
        Identifier recipeIdentifier = EnvironmentzMain.identifierOf("anvil/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_insulated");

        if (!itemStack.is(TagInit.WARM_ARMOR)) {
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.set(ItemInit.INSULATED,true);
            registry.add(new AnvilRecipe(recipeIdentifier, List.of(itemStack), List.of(new ItemStack(ItemInit.POLAR_BEAR_FUR_ITEM)), List.of(itemStack2)));

            itemStack2 = itemStack.copy();
            itemStack2.set(ItemInit.ICED,ItemInit.COOLING_HEATING_VALUE);
            registry.add(new AnvilRecipe(EnvironmentzMain.identifierOf("anvil/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_cooled"), List.of(itemStack), List.of(new ItemStack(Items.ICE)), List.of(itemStack2)));
        }
    }

}
