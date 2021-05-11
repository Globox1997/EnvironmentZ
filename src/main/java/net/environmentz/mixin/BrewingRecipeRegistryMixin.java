package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.init.ItemInit;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "registerDefaults", at = @At("TAIL"))
    private static void registerDefaultsMixin(CallbackInfo info) {
        registerPotionRecipe(Potions.AWKWARD, Items.FIRE_CHARGE, ItemInit.COLD_RESISTANCE);
        registerPotionRecipe(ItemInit.COLD_RESISTANCE, Items.REDSTONE, ItemInit.LONG_COLD_RESISTANCE);
        registerPotionRecipe(Potions.AWKWARD, Items.SNOWBALL, ItemInit.OVERHEATING_RESISTANCE);
        registerPotionRecipe(ItemInit.OVERHEATING_RESISTANCE, Items.REDSTONE, ItemInit.LONG_OVERHEATING_RESISTANCE);
    }

    @Shadow
    private static void registerPotionRecipe(Potion input, Item item, Potion output) {
    }
}
