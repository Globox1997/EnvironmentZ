package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.TemperatureHudRendering;
import net.environmentz.temperature.TemperatureManager;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    @Mutable
    private final MinecraftClient client;

    private int xEnvPosition;
    private int yEnvPosition;
    private int extraEnvPosition;
    private int envIntensity;
    private boolean heat;

    private int thermometerXPosition = 80;
    private int thermometerYPosition = 11;

    public InGameHudMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"))
    private void renderMixin(DrawContext context, float f, CallbackInfo info) {
        // Gets ticked 60 times per second
        PlayerEntity playerEntity = client.player;
        TemperatureManager temperatureManager = ((TemperatureManagerAccess) playerEntity).getTemperatureManager();

        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();

        if (!playerEntity.isCreative() && !playerEntity.isSpectator() && !playerEntity.isInvulnerable()) {
            if (playerEntity.age % 60 == 0 && !client.isPaused()) {
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
            }
            TemperatureHudRendering.renderPlayerTemperatureIcon(context, client, playerEntity, heat, xEnvPosition, yEnvPosition, extraEnvPosition, envIntensity, scaledWidth, scaledHeight);

            if (ConfigInit.CONFIG.showThermometer) {
                if (playerEntity.age % 60 == 0) {
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
                }
                TemperatureHudRendering.renderThermometerIcon(context, client, playerEntity, thermometerXPosition, thermometerYPosition, scaledWidth, scaledHeight);
            }
        } else if ((playerEntity.isCreative() || playerEntity.isSpectator()) && RenderInit.getBlurProgress() > 0.001F) {
            RenderInit.setBlurProgress(0.00F);
        }
    }

}