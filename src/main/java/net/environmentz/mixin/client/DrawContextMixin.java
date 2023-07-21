package net.environmentz.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Inject(method = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    private void drawItemInSlotMixin(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride, CallbackInfo info) {
        if (stack.isIn(TagInit.ARMOR_ITEMS) && stack.hasNbt() && stack.getNbt().contains("iced")) {
            int i = getIcedItemBarStep(stack);
            int j = getIcedItemBarColor(stack);
            int extraY = 0;
            if (stack.isItemBarVisible()) {
                extraY = -2;
            }

            int k = x + 2;
            int l = y + 13;

            this.fill(RenderLayer.getGuiOverlay(), k, l + extraY, k + 13, l + 2 + extraY, -16777216);
            this.fill(RenderLayer.getGuiOverlay(), k, l + extraY, k + i, l + 1 + extraY, j | 0xFF000000);
        }

    }

    // 0.7 is dark blue as hsv value
    // 0.5 is light blue as hsv value
    private static int getIcedItemBarColor(ItemStack stack) {
        float f = Math.max(0.0f, 0.55f - ((float) ItemInit.COOLING_HEATING_VALUE - (float) stack.getNbt().getInt("iced")) / (float) ItemInit.COOLING_HEATING_VALUE * 0.05f);
        return MathHelper.hsvToRgb(f, 1.0f, 1.0f);
    }

    private static int getIcedItemBarStep(ItemStack stack) {
        return Math.round((float) stack.getNbt().getInt("iced") * 13.0f / (float) ItemInit.COOLING_HEATING_VALUE);
    }

    @Shadow
    public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color) {
    }

}
