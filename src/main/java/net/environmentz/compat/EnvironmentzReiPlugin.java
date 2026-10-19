package net.environmentz.compat;

import java.util.List;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.anvil.AnvilRecipe;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnvironmentzReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(TagInit.ARMOR_ITEMS)) {
            addRecipe(registry, registryEntry.value());
        }
    }

    private static void addRecipe(DisplayRegistry registry, Item item) {
        ItemStack itemStack = new ItemStack(item);
        Identifier itemIdentifier = Registries.ITEM.getId(item);
        Identifier recipeIdentifier = EnvironmentzMain.identifierOf("anvil/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_insulated");

        if (registry.getRecipeManager().get(recipeIdentifier).isEmpty()) {
            if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                ItemStack itemStack2 = itemStack.copy();
                itemStack2.set(ItemInit.INSULATED,true);
                registry.add(new AnvilRecipe(recipeIdentifier, List.of(itemStack), List.of(new ItemStack(ItemInit.POLAR_BEAR_FUR_ITEM)), List.of(itemStack2)));

                itemStack2 = itemStack.copy();
                itemStack2.set(ItemInit.ICED,ItemInit.COOLING_HEATING_VALUE);
                registry.add(new AnvilRecipe(EnvironmentzMain.identifierOf("anvil/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_cooled"), List.of(itemStack), List.of(new ItemStack(Items.ICE)), List.of(itemStack2)));
            }
        }
    }

}
