package net.environmentz.mixin.client;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.environmentz.temperature.TemperatureHudRendering;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    @Mutable
    private final MinecraftClient client;

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

    public InGameHudMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        PlayerEntity playerEntity = client.player;
        if (playerEntity != null && playerEntity.getWorld().getTime() % 20 == 0) {
            TemperatureManager temperatureManager = ((TemperatureManagerAccess) playerEntity).getTemperatureManager();

            int playerTemperature = temperatureManager.getPlayerTemperature();
            if (playerTemperature != 0) {
                if (playerTemperature < Temperatures.getBodyTemperatures(2)) {
                    heat = false;
                    if (playerTemperature < Temperatures.getBodyTemperatures(1)) {
                        xEnvPosition = 0;
                        yEnvPosition = 13;
                        envIntensity = Math.abs(playerTemperature) - Math.abs(Temperatures.getBodyTemperatures(1));
                    } else {
                        xEnvPosition = 26;
                        yEnvPosition = 0;
                        envIntensity = Math.abs(playerTemperature) - Math.abs(Temperatures.getBodyTemperatures(2));
                    }
                } else if (playerTemperature > Temperatures.getBodyTemperatures(4)) {
                    heat = true;
                    if (playerTemperature > Temperatures.getBodyTemperatures(5)) {
                        xEnvPosition = 0;
                        yEnvPosition = 26;
                        envIntensity = Math.abs(playerTemperature - Temperatures.getBodyTemperatures(5));
                        if (playerTemperature > Temperatures.getBodyTemperatures(5)) {
                            RenderInit.setBlurProgress(
                                    (float) (playerTemperature - Temperatures.getBodyTemperatures(5)) / (Temperatures.getBodyTemperatures(6) - Temperatures.getBodyTemperatures(5)));
                            // * this.client.options.getDistortionEffectScale().getValue().floatValue()
                        }
                    } else {
                        xEnvPosition = 13;
                        yEnvPosition = 0;
                        envIntensity = Math.abs(playerTemperature - Temperatures.getBodyTemperatures(4));
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
            if (RenderInit.getBlurProgress() > 0.001F && playerTemperature <= Temperatures.getBodyTemperatures(5)) {
                RenderInit.setBlurProgress(0.00F);
            }

            int playerWetIntensityValue = temperatureManager.getPlayerWetIntensityValue();
            if (playerWetIntensityValue <= 0) {
                extraEnvPosition = 0;
            } else {
                extraEnvPosition = playerWetIntensityValue;
            }


            int thermometerTemperature = temperatureManager.getThermometerTemperature();
            if (thermometerTemperature <= Temperatures.getThermometerTemperatures(0)) {
                thermometerXPosition = 112;

            } else if (thermometerTemperature >= Temperatures.getThermometerTemperatures(3)) {
                thermometerXPosition = 96;
            } else {
                thermometerXPosition = 80;
                if (thermometerTemperature <= Temperatures.getThermometerTemperatures(1)) {
                    thermometerYPosition = 18;
                } else if (thermometerTemperature >= Temperatures.getThermometerTemperatures(2)) {
                    thermometerYPosition = 0;
                } else {
                    thermometerYPosition = 11;
                }
            }

            if (playerEntity.getWorld().getTime() % 40 == 0) {
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

    @Inject(method = "renderMainHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"))
    private void renderMainHudMixin(DrawContext context, RenderTickCounter tickCounter, CallbackInfo info) {
        PlayerEntity playerEntity = client.player;

        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();
        if (playerEntity != null) {
            if (!playerEntity.isCreative() && !playerEntity.isSpectator() && !playerEntity.isInvulnerable()) {

                TemperatureHudRendering.renderPlayerTemperatureIcon(context, client, playerEntity, heat, xEnvPosition, yEnvPosition, extraEnvPosition, envIntensity, scaledWidth, scaledHeight);

                if (ConfigInit.CONFIG.showThermometer) {
                    TemperatureHudRendering.renderThermometerIcon(context, client, playerEntity, thermometerXPosition, thermometerYPosition, scaledWidth, scaledHeight);
                }

                if (ConfigInit.CONFIG.showTemperatureArrow && isChangingPlayerTemperature != 0) {
                    TemperatureHudRendering.renderTemperatureArrowIcon(context, client, playerEntity, isChangingPlayerTemperature, scaledWidth, scaledHeight);
                }
            } else if ((playerEntity.isCreative() || playerEntity.isSpectator()) && RenderInit.getBlurProgress() > 0.001F) {
                RenderInit.setBlurProgress(0.00F);
            }
        }
    }

}