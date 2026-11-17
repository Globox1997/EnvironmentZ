package net.environmentz.compat;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class EnvironmentzJeiPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return EnvironmentzMain.identifierOf("jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        List<ItemStack> insulatingItems = stacksOf(TagInit.INSOLATING_ITEM);
        List<ItemStack> iceItems = stacksOf(TagInit.ICE_ITEMS);
        List<IJeiAnvilRecipe> recipes = new ArrayList<>();

        for (Holder<Item> registryEntry : BuiltInRegistries.ITEM.getTagOrEmpty(TagInit.ARMOR_ITEMS)) {
            ItemStack itemStack = new ItemStack(registryEntry.value());
            if (itemStack.is(TagInit.WARM_ARMOR)) {
                continue;
            }
            Identifier itemIdentifier = BuiltInRegistries.ITEM.getKey(registryEntry.value());

            ItemStack insulatedStack = itemStack.copy();
            insulatedStack.set(ItemInit.INSULATED, true);
            recipes.add(factory.createAnvilRecipe(itemStack, insulatingItems, List.of(insulatedStack),
                    EnvironmentzMain.identifierOf("anvil/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_insulated")));

            ItemStack icedStack = itemStack.copy();
            icedStack.set(ItemInit.ICED, ItemInit.COOLING_HEATING_VALUE);
            recipes.add(factory.createAnvilRecipe(itemStack, iceItems, List.of(icedStack),
                    EnvironmentzMain.identifierOf("anvil/" + itemIdentifier.getNamespace() + "/" + itemIdentifier.getPath() + "_cooled")));
        }
        registration.addRecipes(RecipeTypes.ANVIL, recipes);
    }

    private static List<ItemStack> stacksOf(TagKey<Item> tag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Holder<Item> registryEntry : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            stacks.add(new ItemStack(registryEntry.value()));
        }
        return stacks;
    }

}
