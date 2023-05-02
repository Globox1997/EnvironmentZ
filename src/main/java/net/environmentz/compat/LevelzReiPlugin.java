package net.environmentz.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

@Environment(EnvType.CLIENT)
public class LevelzReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (RegistryEntry<Item> registryEntry : Registry.ITEM.iterateEntries(TagInit.ARMOR_ITEMS)) {
            addRecipe(registry, registryEntry.value());
        }
    }

    private static void addRecipe(DisplayRegistry registry, Item item) {
        ItemStack itemStack = new ItemStack(item);
        Identifier recipeIdentifier = new Identifier(Registry.ITEM.getId(item).getPath() + "_fur_insolated");

        if (registry.getRecipeManager().get(recipeIdentifier).isEmpty()) {
            if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                ItemStack itemStack2 = itemStack.copy();
                NbtCompound nbt = itemStack2.getOrCreateNbt();
                nbt.putString("environmentz", "fur_insolated");
                itemStack2.setNbt(nbt);
                registry.add(new SmithingRecipe(recipeIdentifier, Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.INSOLATING_ITEM), itemStack2));

                itemStack2 = itemStack.copy();
                nbt = itemStack2.getOrCreateNbt();
                nbt.putInt("iced", ItemInit.COOLING_HEATING_VALUE);
                itemStack2.setNbt(nbt);
                registry.add(new SmithingRecipe(new Identifier(Registry.ITEM.getId(item).getPath() + "_cooled"), Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.ICE_ITEMS), itemStack2));
            }
        }
    }

}
