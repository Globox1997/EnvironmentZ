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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public class DrawContextMixin {

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isBarVisible()Z"))
    private void drawItemInSlotMixin(Font textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride, CallbackInfo info) {
        if (stack.is(TagInit.ARMOR_ITEMS) && stack.get(ItemInit.ICED) != null) {
            int i = getIcedItemBarStep(stack);
            int j = getIcedItemBarColor(stack);
            int extraY = 0;
            if (stack.isBarVisible()) {
                extraY = -2;
            }

            int k = x + 2;
            int l = y + 13;

            this.fill(RenderType.guiOverlay(), k, l + extraY, k + 13, l + 2 + extraY, -16777216);
            this.fill(RenderType.guiOverlay(), k, l + extraY, k + i, l + 1 + extraY, j | 0xFF000000);
        }

    }

    // 0.7 is dark blue as hsv value
    // 0.5 is light blue as hsv value
    private static int getIcedItemBarColor(ItemStack stack) {
        float f = Math.max(0.0f, 0.55f - ((float) ItemInit.COOLING_HEATING_VALUE - (float) stack.get(ItemInit.ICED)) / (float) ItemInit.COOLING_HEATING_VALUE * 0.05f);
        return Mth.hsvToRgb(f, 1.0f, 1.0f);
    }

    private static int getIcedItemBarStep(ItemStack stack) {
        return Math.round((float) stack.get(ItemInit.ICED) * 13.0f / (float) ItemInit.COOLING_HEATING_VALUE);
    }

    @Shadow
    public void fill(RenderType layer, int x1, int y1, int x2, int y2, int color) {
    }

}
