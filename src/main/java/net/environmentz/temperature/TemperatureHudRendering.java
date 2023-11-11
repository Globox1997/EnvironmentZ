package net.environmentz.temperature;

import com.mojang.blaze3d.systems.RenderSystem;

import net.environmentz.init.ConfigInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TemperatureHudRendering {

    private static final Identifier INDICATOR_ICON = new Identifier("environmentz:textures/gui/indicator_icon.png");

    public static void renderPlayerTemperatureIcon(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, boolean heat, int xValue, int yValue, int extra, int intensity,
            int scaledWidth, int scaledHeight) {

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (yValue != 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            // Background
            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.iconX, scaledHeight - ConfigInit.CONFIG.iconY, xValue + (extra != 0 ? 26 : 0), yValue, 13, 13, 256, 256);

            // Foregound
            float smooth = heat ? ((float) intensity / (float) (Temperatures.getBodyTemperatures(6) - Temperatures.getBodyTemperatures(5)))
                    : ((float) intensity / (float) (Math.abs(Temperatures.getBodyTemperatures(0)) - Math.abs(Temperatures.getBodyTemperatures(1))));

            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.iconX, scaledHeight - ConfigInit.CONFIG.iconY + (13 - iconTextureSplitValue(smooth)), 13,
                    yValue - (iconTextureSplitValue(smooth) - 13), 13, iconTextureSplitValue(smooth), 256, 256);

        } else {
            // body rendering
            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.iconX, scaledHeight - ConfigInit.CONFIG.iconY, 0, 0, 13, 13, 256, 256);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, heat ? ((float) intensity / (float) (Temperatures.getBodyTemperatures(5) - Temperatures.getBodyTemperatures(4)))
                    : ((float) intensity / (float) (Math.abs(Temperatures.getBodyTemperatures(1)) - Math.abs(Temperatures.getBodyTemperatures(2)))));

            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.iconX, scaledHeight - ConfigInit.CONFIG.iconY, xValue, yValue, 13, 13, 256, 256);
        }
        // wetness border
        if (extra != 0 && yValue == 0) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) extra / Temperatures.getBodyWetness(0));
            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.iconX, scaledHeight - ConfigInit.CONFIG.iconY, 39, 0, 13, 13, 256, 256);
        }
    }

    public static void renderThermometerIcon(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, int xValue, int yValue, int scaledWidth, int scaledHeight) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Foregound
        if (xValue == 112 || xValue == 96)
            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.thermometerIconX, scaledHeight - ConfigInit.CONFIG.thermometerIconY, xValue, 0, 16, 32, 256, 256);
        else {
            // Background
            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.thermometerIconX, scaledHeight - ConfigInit.CONFIG.thermometerIconY, 64, 0, 16, 32, 256, 256);
            // Foregound
            context.drawTexture(INDICATOR_ICON, (scaledWidth / 2) - ConfigInit.CONFIG.thermometerIconX, scaledHeight - ConfigInit.CONFIG.thermometerIconY + yValue, xValue, yValue, 16, 32 - yValue,
                    256, 256);
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
        } else {
            return 0;
        }
    }

}