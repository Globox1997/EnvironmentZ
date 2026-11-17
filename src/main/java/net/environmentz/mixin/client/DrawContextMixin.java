package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphicsExtractor.class)
public class DrawContextMixin {

    @Inject(method = "itemBar", at = @At("HEAD"))
    private void drawItemInSlotMixin(ItemStack stack, int x, int y, CallbackInfo info) {
        if (stack.is(TagInit.ARMOR_ITEMS) && stack.get(ItemInit.ICED) != null) {
            int i = getIcedItemBarStep(stack);
            int j = getIcedItemBarColor(stack);
            int extraY = 0;
            if (stack.isBarVisible()) {
                extraY = -2;
            }

            int k = x + 2;
            int l = y + 13;

            GuiGraphicsExtractor graphics = (GuiGraphicsExtractor) (Object) this;
            graphics.fill(RenderPipelines.GUI, k, l + extraY, k + 13, l + 2 + extraY, -16777216);
            graphics.fill(RenderPipelines.GUI, k, l + extraY, k + i, l + 1 + extraY, j | 0xFF000000);
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

}
