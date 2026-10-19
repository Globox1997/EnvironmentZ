package net.environmentz.compat;

import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EnvironmentzEmiPlugin implements EmiPlugin {

    private static List<Item> itemList = new ArrayList<Item>();

    private static void addRecipe(EmiRegistry registry, Item item) {
        if (!itemList.contains(item)) {
            ItemStack itemStack = new ItemStack(item);
            Identifier itemIdentifier = Registries.ITEM.getId(item);
            Identifier recipeIdentifier = EnvironmentzMain.identifierOf("/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_insulated");

            if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                ItemStack itemStack2 = itemStack.copy();
                itemStack2.set(ItemInit.INSULATED,true);
                registry.addRecipe(new EnvironmentEmiAnvilRecipe(EmiStack.of(itemStack), EmiIngredient.of(TagInit.INSOLATING_ITEM), EmiStack.of(itemStack2), recipeIdentifier));

                itemStack2 = itemStack.copy();
                itemStack2.set(ItemInit.ICED,ItemInit.COOLING_HEATING_VALUE);
                registry.addRecipe(new EnvironmentEmiAnvilRecipe(EmiStack.of(itemStack), EmiIngredient.of(TagInit.ICE_ITEMS), EmiStack.of(itemStack2),
                        EnvironmentzMain.identifierOf("/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_cooled")));

                itemList.add(item);
            }
        }
    }

    @Override
    public void register(EmiRegistry registry) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(TagInit.ARMOR_ITEMS)) {
            addRecipe(registry, registryEntry.value());
        }
    }

}
