package net.environmentz.compat;

import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class LevelzEmiPlugin implements EmiPlugin {

    private static List<Item> itemList = new ArrayList<Item>();

    private static void addRecipe(EmiRegistry registry, Item item) {
        if (!itemList.contains(item)) {
            ItemStack itemStack = new ItemStack(item);
            Identifier recipeIdentifier = new Identifier(Registry.ITEM.getId(item).getPath() + "_fur_insolated");

            if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                ItemStack itemStack2 = itemStack.copy();
                NbtCompound nbt = itemStack2.getOrCreateNbt();
                nbt.putString("environmentz", "fur_insolated");
                itemStack2.setNbt(nbt);
                registry.addRecipe(new EmiSmithingRecipe(new SmithingRecipe(recipeIdentifier, Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.INSOLATING_ITEM), itemStack2)));
                itemList.add(item);

                itemStack2 = itemStack.copy();
                nbt = itemStack2.getOrCreateNbt();
                nbt.putInt("iced", ItemInit.COOLING_HEATING_VALUE);
                itemStack2.setNbt(nbt);
                registry.addRecipe(new EmiSmithingRecipe(
                        new SmithingRecipe(new Identifier(Registry.ITEM.getId(item).getPath() + "_cooled"), Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.ICE_ITEMS), itemStack2)));
                itemList.add(item);
            }
        }
    }

    @Override
    public void register(EmiRegistry registry) {
        for (RegistryEntry<Item> registryEntry : Registry.ITEM.iterateEntries(TagInit.ARMOR_ITEMS)) {
            addRecipe(registry, registryEntry.value());
        }
    }

}
