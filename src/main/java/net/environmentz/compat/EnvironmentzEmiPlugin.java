package net.environmentz.compat;

import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EnvironmentzEmiPlugin implements EmiPlugin {

    private static List<Item> itemList = new ArrayList<Item>();

    private static void addRecipe(EmiRegistry registry, Item item) {
        if (!itemList.contains(item)) {
            ItemStack itemStack = new ItemStack(item);
            Identifier recipeIdentifier = new Identifier(Registries.ITEM.getId(item).getPath() + "_fur_insolated");

            if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                ItemStack itemStack2 = itemStack.copy();
                NbtCompound nbt = itemStack2.getOrCreateNbt();
                nbt.putString("environmentz", "fur_insolated");
                itemStack2.setNbt(nbt);
                registry.addRecipe(new EnvironmentEmiAnvilRecipe(EmiStack.of(itemStack), EmiIngredient.of(TagInit.INSOLATING_ITEM), EmiStack.of(itemStack2), recipeIdentifier));

                itemStack2 = itemStack.copy();
                nbt = itemStack2.getOrCreateNbt();
                nbt.putInt("iced", ItemInit.COOLING_HEATING_VALUE);
                itemStack2.setNbt(nbt);

                registry.addRecipe(new EnvironmentEmiAnvilRecipe(EmiStack.of(itemStack), EmiIngredient.of(TagInit.ICE_ITEMS), EmiStack.of(itemStack2),
                        new Identifier(Registries.ITEM.getId(item).getPath() + "_cooled")));

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
