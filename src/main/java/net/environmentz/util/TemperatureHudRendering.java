package net.environmentz.util;

import com.mojang.blaze3d.systems.RenderSystem;

import net.environmentz.effect.ColdEffect;
import net.environmentz.effect.OverheatingEffect;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.EffectInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class TemperatureHudRendering {

    private static final Identifier FREEZING_ICON = new Identifier("environmentz:textures/gui/coldness.png");
    private static final Identifier OVERHEATING_ICON = new Identifier("environmentz:textures/gui/overheating.png");

    public static float smoothFreezingIconRendering;
    public static float smoothThirstRendering;
    public static boolean isInColdBiome;
    public static boolean isInHotBiome;

    public static void hotEnvRendering(PlayerEntity playerEntity, int wetTimer) {
        isInHotBiome = true;
        if (OverheatingEffect.wearsArmor(playerEntity) && playerEntity.world.isSkyVisible(playerEntity.getBlockPos())
                && ((int) playerEntity.world.getTimeOfDay() > 23000
                        || (int) playerEntity.world.getTimeOfDay() < 12000)) {
            float wetBonus = 0.0F;
            if (playerEntity.hasStatusEffect(EffectInit.WET) && wetTimer == ConfigInit.CONFIG.wet_bonus_malus) {
                wetBonus = 0.5F;
            }
            if (smoothThirstRendering < 1.0F) {
                smoothThirstRendering += (1.0F
                        / ((float) (ConfigInit.CONFIG.overheating_tick_interval * (1F + wetBonus))));
            }
            if (smoothThirstRendering > 1.0F) {
                smoothThirstRendering = 1.0F;
            }
        } else if (smoothThirstRendering > 0.0F) {
            smoothThirstRendering -= (1.0F / ((float) ConfigInit.CONFIG.cooling_down_interval));
        }
    }

    public static void coldEnvRendering(PlayerEntity playerEntity, int wetTimer) {
        isInColdBiome = true;
        int warmClothingModifier = ColdEffect.warmClothingModifier(playerEntity);
        if (!playerEntity.hasStatusEffect(EffectInit.WARMING)
                && warmClothingModifier != (ConfigInit.CONFIG.warm_armor_tick_modifier * 4)
                && !ColdEffect.isWarmBlockNearBy(playerEntity)) {
            float wetMalus = 1.0F;
            if (playerEntity.hasStatusEffect(EffectInit.WET) && wetTimer == ConfigInit.CONFIG.wet_bonus_malus) {
                wetMalus = 0.5F;
            }
            if (smoothFreezingIconRendering < 1.0F) {
                smoothFreezingIconRendering += (1.0F
                        / ((float) (ConfigInit.CONFIG.cold_tick_interval + warmClothingModifier) * wetMalus));
            }
            if (smoothFreezingIconRendering > 1.0F) {
                smoothFreezingIconRendering = 1.0F;
            }
        } else if (smoothFreezingIconRendering > 0.0F) {
            smoothFreezingIconRendering -= (1.0F / ((float) ConfigInit.CONFIG.heating_up_interval));
        }
    }

    public static void renderTemperatureAspect(MatrixStack matrixStack, PlayerEntity playerEntity,
            MinecraftClient client) {
        if (!ConfigInit.CONFIG.excluded_cold_names.contains(playerEntity.getName().asString())
                && (isInColdBiome && smoothFreezingIconRendering > 0.0F)) {
            renderIconBackgroundOverlay(matrixStack, FREEZING_ICON, client);
            if (smoothFreezingIconRendering > 0.0F) {
                renderIconOverlay(matrixStack, smoothFreezingIconRendering, FREEZING_ICON, client);
            }
        } else if (!ConfigInit.CONFIG.excluded_heat_names.contains(playerEntity.getName().asString())
                && (isInHotBiome && smoothThirstRendering > 0.0F)) {
            renderIconBackgroundOverlay(matrixStack, OVERHEATING_ICON, client);
            if (smoothThirstRendering > 0.0F) {
                renderIconOverlay(matrixStack, smoothThirstRendering, OVERHEATING_ICON, client);
            }
        }
    }

    public static void renderIconOverlay(MatrixStack matrixStack, float smooth, Identifier identifier,
            MinecraftClient client) {
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(identifier);
        DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x,
                scaledHeight - ConfigInit.CONFIG.icon_y + (13 - iconTexture(smooth)), 13.0F,
                13.0F - (iconTexture(smooth) - 13), 13, iconTexture(smooth), 26, 13);
    }

    public static void renderIconBackgroundOverlay(MatrixStack matrixStack, Identifier identifier,
            MinecraftClient client) {
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        client.getTextureManager().bindTexture(identifier);
        DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x,
                scaledHeight - ConfigInit.CONFIG.icon_y, 0.0F, 0.0F, 13, 13, 26, 13);
    }

    private static int iconTexture(float smooth) {
        if (smooth >= 0.85F) {
            return 13;
        } else if (smooth > 0.68F) {
            return 10;
        } else if (smooth > 0.51F) {
            return 8;
        } else if (smooth > 0.34F) {
            return 5;
        } else if (smooth > 0.17F) {
            return 3;
        } else
            return 0;
    }

}