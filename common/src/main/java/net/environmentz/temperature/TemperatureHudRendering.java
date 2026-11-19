package net.environmentz.temperature;

import net.environmentz.EnvironmentzMain;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class TemperatureHudRendering {

    private static final Identifier INDICATOR_ICON = EnvironmentzMain.identifierOf("textures/gui/indicator_icon.png");

    private static int xEnvPosition;
    private static int yEnvPosition;
    private static int extraEnvPosition;
    private static int envIntensity;
    private static boolean heat;
    private static int thermometerXPosition = 80;
    private static int thermometerYPosition = 11;
    private static int oldPlayerTemperature = 0;
    private static int isChangingPlayerTemperature = 0;

    public static void tick(Minecraft minecraft) {
        Player playerEntity = minecraft.player;
        if (playerEntity != null && playerEntity.level().getGameTime() % 20 == 0) {
            TemperatureManager temperatureManager = ((TemperatureManagerAccess) playerEntity).getTemperatureManager();

            int playerTemperature = temperatureManager.getPlayerTemperature();
            if (playerTemperature != 0) {
                if (playerTemperature <= Temperatures.getBodyTemperature().minCold()) {
                    heat = false;
                    if (playerTemperature <= Temperatures.getBodyTemperature().maxCold()) {
                        xEnvPosition = 0;
                        yEnvPosition = 13;
                        envIntensity = Math.abs(playerTemperature) - Math.abs(Temperatures.getBodyTemperature().maxCold());
                    } else {
                        xEnvPosition = 26;
                        yEnvPosition = 0;
                        envIntensity = Math.abs(playerTemperature) - Math.abs(Temperatures.getBodyTemperature().minCold());
                    }
                } else if (playerTemperature >= Temperatures.getBodyTemperature().minHot()) {
                    heat = true;
                    if (playerTemperature >= Temperatures.getBodyTemperature().maxHot()) {
                        xEnvPosition = 0;
                        yEnvPosition = 26;
                        envIntensity = Math.abs(playerTemperature - Temperatures.getBodyTemperature().maxHot());
                        if (playerTemperature > Temperatures.getBodyTemperature().maxHot()) {
                            RenderInit.setBlurProgress(
                                    (float) (playerTemperature - Temperatures.getBodyTemperature().maxHot()) / (Temperatures.getBodyTemperature().maxVeryHot() - Temperatures.getBodyTemperature().maxHot()));
                        }
                    } else {
                        xEnvPosition = 13;
                        yEnvPosition = 0;
                        envIntensity = Math.abs(playerTemperature - Temperatures.getBodyTemperature().minHot());
                    }
                } else {
                    xEnvPosition = 0;
                    yEnvPosition = 0;
                    envIntensity = 0;
                }
            } else {
                xEnvPosition = 0;
                yEnvPosition = 0;
                envIntensity = 0;
            }
            if (RenderInit.getBlurProgress() > 0.001F && playerTemperature <= Temperatures.getBodyTemperature().maxHot()) {
                RenderInit.setBlurProgress(0.00F);
            }

            int playerWetIntensityValue = temperatureManager.getPlayerWetIntensityValue();
            extraEnvPosition = Math.max(playerWetIntensityValue, 0);

            int thermometerTemperature = temperatureManager.getThermometerTemperature();
            if (thermometerTemperature <= Temperatures.getThermometerTemperature().veryCold()) {
                thermometerXPosition = 112;
            } else if (thermometerTemperature >= Temperatures.getThermometerTemperature().veryHot()) {
                thermometerXPosition = 96;
            } else {
                thermometerXPosition = 80;
                if (thermometerTemperature <= Temperatures.getThermometerTemperature().cold()) {
                    thermometerYPosition = 18;
                } else if (thermometerTemperature >= Temperatures.getThermometerTemperature().hot()) {
                    thermometerYPosition = 0;
                } else {
                    thermometerYPosition = 11;
                }
            }

            if (playerEntity.level().getGameTime() % 40 == 0) {
                if (oldPlayerTemperature < temperatureManager.getPlayerTemperature() - 10) {
                    isChangingPlayerTemperature = 1;
                } else if (oldPlayerTemperature > temperatureManager.getPlayerTemperature() + 10) {
                    isChangingPlayerTemperature = 2;
                } else {
                    isChangingPlayerTemperature = 0;
                }
                oldPlayerTemperature = temperatureManager.getPlayerTemperature();
            }
        }
    }

    public static void render(GuiGraphicsExtractor context) {
        Minecraft minecraft = Minecraft.getInstance();
        Player playerEntity = minecraft.player;
        if (playerEntity == null) {
            return;
        }
        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        if (!playerEntity.isCreative() && !playerEntity.isSpectator() && !playerEntity.isInvulnerable()) {
            renderPlayerTemperatureIcon(context, heat, xEnvPosition, yEnvPosition, extraEnvPosition, envIntensity, scaledWidth, scaledHeight);

            if (ConfigInit.CONFIG.showThermometer) {
                renderThermometerIcon(context, thermometerXPosition, thermometerYPosition, scaledWidth, scaledHeight);
            }

            if (ConfigInit.CONFIG.showTemperatureArrow && isChangingPlayerTemperature != 0) {
                renderTemperatureArrowIcon(context, isChangingPlayerTemperature, scaledWidth, scaledHeight);
            }
        } else if ((playerEntity.isCreative() || playerEntity.isSpectator()) && RenderInit.getBlurProgress() > 0.001F) {
            RenderInit.setBlurProgress(0.00F);
        }
    }

    private static void blit(GuiGraphicsExtractor context, int x, int y, int u, int v, int width, int height, float alpha) {
        context.blit(RenderPipelines.GUI_TEXTURED, INDICATOR_ICON, x, y, u, v, width, height, 256, 256, ARGB.white(Mth.clamp(alpha, 0.0F, 1.0F)));
    }

    private static void renderPlayerTemperatureIcon(GuiGraphicsExtractor context, boolean heat, int xValue, int yValue, int extra, int intensity, int scaledWidth, int scaledHeight) {
        int x = (scaledWidth / 2) - ConfigInit.CONFIG.iconX;
        int y = scaledHeight - ConfigInit.CONFIG.iconY;
        if (yValue != 0) {
            blit(context, x, y, xValue + (extra != 0 ? 26 : 0), yValue, 13, 13, 1.0F);

            float smooth = heat ? ((float) intensity / (float) (Temperatures.getBodyTemperature().maxVeryHot() - Temperatures.getBodyTemperature().maxHot()))
                    : ((float) intensity / (float) (Math.abs(Temperatures.getBodyTemperature().maxVeryCold()) - Math.abs(Temperatures.getBodyTemperature().maxCold())));

            blit(context, x, y + (13 - iconTextureSplitValue(smooth)), 13, yValue - (iconTextureSplitValue(smooth) - 13), 13, iconTextureSplitValue(smooth), 1.0F);
        } else {
            blit(context, x, y, 0, 0, 13, 13, 1.0F);
            float alpha = heat ? ((float) intensity / (float) (Temperatures.getBodyTemperature().maxHot() - Temperatures.getBodyTemperature().minHot()))
                    : ((float) intensity / (float) (Math.abs(Temperatures.getBodyTemperature().maxCold()) - Math.abs(Temperatures.getBodyTemperature().minCold())));
            blit(context, x, y, xValue, yValue, 13, 13, alpha);
        }
        if (extra != 0 && yValue == 0) {
            blit(context, x, y, 39, 0, 13, 13, (float) extra / Temperatures.getBodyWetness().max());
        }
    }

    private static void renderThermometerIcon(GuiGraphicsExtractor context, int xValue, int yValue, int scaledWidth, int scaledHeight) {
        int x = (scaledWidth / 2) - ConfigInit.CONFIG.thermometerIconX;
        int y = scaledHeight - ConfigInit.CONFIG.thermometerIconY;
        if (xValue == 112 || xValue == 96) {
            blit(context, x, y, xValue, 0, 16, 32, 1.0F);
        } else {
            blit(context, x, y, 64, 0, 16, 32, 1.0F);
            blit(context, x, y + yValue, xValue, yValue, 16, 32 - yValue, 1.0F);
        }
    }

    private static void renderTemperatureArrowIcon(GuiGraphicsExtractor context, int isChangingPlayerTemperature, int scaledWidth, int scaledHeight) {
        blit(context, (scaledWidth / 2) - ConfigInit.CONFIG.temperatureArrowIconX, scaledHeight - ConfigInit.CONFIG.temperatureArrowIconY, (isChangingPlayerTemperature == 1 ? 0 : 7), 39, 7, 9,
                1.0F);
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
