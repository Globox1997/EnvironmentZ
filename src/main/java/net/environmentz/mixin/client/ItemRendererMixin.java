package net.environmentz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"))
    private void renderGuiItemOverlayMixin(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {
        if (stack.isIn(TagInit.ARMOR_ITEMS) && stack.hasNbt() && stack.getNbt().contains("iced")) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            int i = getIcedItemBarStep(stack);
            int j = getIcedItemBarColor(stack);
            int extraY = 13;
            if (stack.isItemBarVisible()) {
                extraY = 11;
            }
            this.renderGuiQuad(bufferBuilder, x + 2, y + extraY, 13, 2, 0, 0, 0, 255);
            this.renderGuiQuad(bufferBuilder, x + 2, y + extraY, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
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
    private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
    }

}
