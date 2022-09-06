package net.environmentz.compat;

import java.util.Iterator;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

@Environment(EnvType.CLIENT)
public class LevelzReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {

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

                        registry.add(new SmithingRecipe(Registry.ITEM.getId(itemStack.getItem()), Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.INSOLATING_ITEM), itemStack2));
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

                    registry.add(new SmithingRecipe(Registry.ITEM.getId(itemStack.getItem()), Ingredient.ofStacks(itemStack), Ingredient.fromTag(TagInit.INSOLATING_ITEM), itemStack2));
                }
            }
        }
    }

}
