package net.environmentz.mixin.client;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.environmentz.temperature.TemperatureHudRendering;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    @Mutable
    private final Minecraft minecraft;

    @Unique
    private int xEnvPosition;
    @Unique
    private int yEnvPosition;
    @Unique
    private int extraEnvPosition;
    @Unique
    private int envIntensity;
    @Unique
    private boolean heat;
    @Unique
    private int thermometerXPosition = 80;
    @Unique
    private int thermometerYPosition = 11;
    @Unique
    private int oldPlayerTemperature = 0;
    @Unique // 0: no change, 1: increase, 2: decrease
    private int isChangingPlayerTemperature = 0;

    public InGameHudMixin(Minecraft client) {
        this.minecraft = client;
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
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
                            // * this.client.options.getDistortionEffectScale().getValue().floatValue()
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
            if (playerWetIntensityValue <= 0) {
                extraEnvPosition = 0;
            } else {
                extraEnvPosition = playerWetIntensityValue;
            }


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

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void renderMainHudMixin(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo info) {
        Player playerEntity = minecraft.player;

        int scaledWidth = minecraft.getWindow().getGuiScaledWidth();
        int scaledHeight = minecraft.getWindow().getGuiScaledHeight();
        if (playerEntity != null) {
            if (!playerEntity.isCreative() && !playerEntity.isSpectator() && !playerEntity.isInvulnerable()) {

                TemperatureHudRendering.renderPlayerTemperatureIcon(context, minecraft, playerEntity, heat, xEnvPosition, yEnvPosition, extraEnvPosition, envIntensity, scaledWidth, scaledHeight);

                if (ConfigInit.CONFIG.showThermometer) {
                    TemperatureHudRendering.renderThermometerIcon(context, minecraft, playerEntity, thermometerXPosition, thermometerYPosition, scaledWidth, scaledHeight);
                }

                if (ConfigInit.CONFIG.showTemperatureArrow && isChangingPlayerTemperature != 0) {
                    TemperatureHudRendering.renderTemperatureArrowIcon(context, minecraft, playerEntity, isChangingPlayerTemperature, scaledWidth, scaledHeight);
                }
            } else if ((playerEntity.isCreative() || playerEntity.isSpectator()) && RenderInit.getBlurProgress() > 0.001F) {
                RenderInit.setBlurProgress(0.00F);
            }
        }
    }

}