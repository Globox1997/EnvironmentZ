package net.environmentz.util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.environmentz.init.ConfigInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TemperatureHudRendering {

    private static final Identifier INDICATOR_ICON = new Identifier("environmentz:textures/gui/indicator_icon.png");

    public static void renderPlayerEnvironmentIcon(MatrixStack matrixStack, MinecraftClient client, PlayerEntity playerEntity, int xValue, int yValue, int extra, int intensity) {
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();

        RenderSystem.setShaderColor(0.3F + (float) intensity / 120F, 0.3F + (float) intensity / 120F, 0.3F + (float) intensity / 120F, 1.0F);
        RenderSystem.setShaderTexture(0, INDICATOR_ICON);
        if (yValue != 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            // Background
            DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x, scaledHeight - ConfigInit.CONFIG.icon_y, xValue + (extra != 0 ? 26 : 0), yValue, 13, 13, 256, 256);

            // Foregound
            DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x, scaledHeight - ConfigInit.CONFIG.icon_y + (13 - iconTextureSplitValue((float) intensity / 120F)), 13,
                    yValue - (iconTextureSplitValue((float) intensity / 120F) - 13), 13, iconTextureSplitValue((float) intensity / 120F), 256, 256);

            // if (client.player.world.getTime() % 20 == 0)
            // System.out.println("HUD: " + yValue + "::" + iconTextureSplitValue((float) intensity / 120F));

        } else {
            if (xValue == 0)
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x, scaledHeight - ConfigInit.CONFIG.icon_y, xValue, yValue, 13, 13, 256, 256);
        }

        if (extra != 0 && yValue == 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) extra / 120.0F);
            DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x, scaledHeight - ConfigInit.CONFIG.icon_y, 39, 0, 13, 13, 256, 256);
        }
    }

    private static int iconTextureSplitValue(float smooth) {
        if (smooth >= 0.99F) {
            return 13;
        } else if (smooth > 0.79F) {
            return 10;
        } else if (smooth > 0.59F) {
            return 8;
        } else if (smooth > 0.39F) {
            return 5;
        } else if (smooth > 0.19F) {
            return 3;
        } else
            return 0;
    }

}