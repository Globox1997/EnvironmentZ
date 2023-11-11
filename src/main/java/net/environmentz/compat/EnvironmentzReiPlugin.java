package net.environmentz.compat;

import java.util.List;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.anvil.AnvilRecipe;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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
        Identifier recipeIdentifier = new Identifier(Registries.ITEM.getId(item).getPath() + "_fur_insolated");

        if (registry.getRecipeManager().get(recipeIdentifier).isEmpty()) {
            if (!itemStack.isIn(TagInit.WARM_ARMOR)) {
                ItemStack itemStack2 = itemStack.copy();
                NbtCompound nbt = itemStack2.getOrCreateNbt();
                nbt.putString("environmentz", "fur_insolated");
                itemStack2.setNbt(nbt);
                registry.add(new AnvilRecipe(recipeIdentifier, List.of(itemStack), List.of(new ItemStack(ItemInit.POLAR_BEAR_FUR_ITEM)), List.of(itemStack2)));

                itemStack2 = itemStack.copy();
                nbt = itemStack2.getOrCreateNbt();
                nbt.putInt("iced", ItemInit.COOLING_HEATING_VALUE);
                itemStack2.setNbt(nbt);
                registry.add(new AnvilRecipe(new Identifier(Registries.ITEM.getId(item).getPath() + "_cooled"), List.of(itemStack), List.of(new ItemStack(Items.ICE)), List.of(itemStack2)));
            }
        }
    }

}
