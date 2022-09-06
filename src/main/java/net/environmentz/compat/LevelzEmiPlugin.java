package net.environmentz.compat;

import java.util.Iterator;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import net.environmentz.init.TagInit;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class LevelzEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {

        if (!TagInit.isAutoTagLoaded) {
            Iterator<Item> iterator = Registry.ITEM.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (item instanceof ArmorItem) {
                    ItemStack itemStack = new ItemStack(item);

                    if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                        ItemStack itemStack2 = itemStack.copy();
                        if (!itemStack2.hasNbt()) {
                            NbtCompound nbt = new NbtCompound();
                            nbt.putString("environmentz", "fur_insolated");
                            itemStack2.setNbt(nbt);
                        } else
                            itemStack2.getNbt().putString("environmentz", "fur_insolated");

                        registry.addRecipe(new EmiSmithingRecipe(
                                new SmithingRecipe(Registry.ITEM.getId(itemStack.getItem()), Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.INSOLATING_ITEM), itemStack2)));
                    }
                } else
                    continue;
            }
        } else {
            for (RegistryEntry<Item> registryEntry : Registry.ITEM.iterateEntries(TagInit.ARMOR_ITEMS)) {
                ItemStack itemStack = new ItemStack(registryEntry);

                if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                    ItemStack itemStack2 = itemStack.copy();
                    if (!itemStack2.hasNbt()) {
                        NbtCompound nbt = new NbtCompound();
                        nbt.putString("environmentz", "fur_insolated");
                        itemStack2.setNbt(nbt);
                    } else
                        itemStack2.getNbt().putString("environmentz", "fur_insolated");

                    registry.addRecipe(new EmiSmithingRecipe(
                            new SmithingRecipe(Registry.ITEM.getId(itemStack.getItem()), Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.INSOLATING_ITEM), itemStack2)));
                }
            }
        }
    }

}
