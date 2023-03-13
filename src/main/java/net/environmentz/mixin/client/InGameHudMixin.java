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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

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

    @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
    private void renderMixin(MatrixStack matrixStack, float f, CallbackInfo info) {
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
            TemperatureHudRendering.renderPlayerTemperatureIcon(matrixStack, client, playerEntity, heat, xEnvPosition, yEnvPosition, extraEnvPosition, envIntensity, scaledWidth, scaledHeight);

            // Thermometer needs rework
            // Add a sourrding integer at TemperatureAspects and send to client
            if (ConfigInit.CONFIG.show_thermometer) {
                if (playerEntity.age % 60 == 0) {
                    int thermometerTemperature = temperatureManager.getThermometerTemperature();
                    // float biomeTemperature = client.world.getBiome(playerEntity.getBlockPos()).value().getTemperature();
                    // int playerPositionHeight = playerEntity.getBlockY();
                    // boolean isDay = client.world.getTimeOfDay() % 24000 > 0 && client.world.getTimeOfDay() % 24000 < 12000;
                    // boolean isShadow = playerEntity.world.isSkyVisible(playerEntity.getBlockPos());
                    // boolean hasFixedTime = client.world.getDimension().hasFixedTime();
                    // int outsideTemperature = 0;

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
                TemperatureHudRendering.renderThermometerIcon(matrixStack, client, playerEntity, thermometerXPosition, thermometerYPosition, scaledWidth, scaledHeight);
            }
            // // Thermometer needs rework
            // // Add a sourrding integer at TemperatureAspects and send to client
            // if (ConfigInit.CONFIG.show_thermometer) {
            // if (playerEntity.age % 60 == 0) {

            // float biomeTemperature = client.world.getBiome(playerEntity.getBlockPos()).value().getTemperature();
            // int playerPositionHeight = playerEntity.getBlockY();
            // boolean isDay = client.world.getTimeOfDay() % 24000 > 0 && client.world.getTimeOfDay() % 24000 < 12000;
            // boolean isShadow = playerEntity.world.isSkyVisible(playerEntity.getBlockPos());
            // boolean hasFixedTime = client.world.getDimension().hasFixedTime();
            // int outsideTemperature = 0;

            // // environmentCode 0: very_low, 1: low, 2: high, 3: very_high, 4: very_low_height, 5: low_height, 6: high_height, 7: very_high_height
            // // public static int getDimensionHeightTemperatures(Identifier dimensionIdentifier, int environmentCode) {

            // // "height": {
            // // "very_low": 3,
            // // "very_low_height": 0,
            // // "low": 1,
            // // "low_height": 30,
            // // "high": -2,
            // // "high_height": 120,
            // // "very_high": -3,
            // // "very_high_height": 190
            // // }

            // // System.out.println(client.world.getDimension().hasFixedTime());

            // if (biomeTemperature <= Temperatures.getBiomeTemperatures(0)) {
            // // && playerPositionHeight >= 0
            // thermometerXPosition = 112;
            // } else if ((biomeTemperature >= ConfigInit.CONFIG.biome_overheat_temp && playerPositionHeight >= 0 && playerPositionHeight < playerEntity.world.getDimension().height() / 0.9f
            // && playerEntity.world.isSkyVisible(playerEntity.getBlockPos()) && isDay) || playerEntity.world.getDimension().ultrawarm()) {
            // thermometerXPosition = 96;
            // } else {
            // thermometerXPosition = 80;
            // if (biomeTemperature <= ConfigInit.CONFIG.biome_cold_temp) {
            // thermometerYPosition = 18;
            // if (playerPositionHeight > playerEntity.world.getDimension().height() / 0.9f) {
            // thermometerXPosition = 112;
            // }
            // } else if (biomeTemperature >= ConfigInit.CONFIG.biome_hot_temp && isDay && playerEntity.world.isSkyVisible(playerEntity.getBlockPos())) {
            // thermometerYPosition = 0;
            // } else {
            // thermometerYPosition = 11;
            // }
            // }

            // // if (biomeTemperature <= ConfigInit.CONFIG.biome_freeze_temp && playerPositionHeight >= 0) {
            // // thermometerXPosition = 112;
            // // } else if ((biomeTemperature >= ConfigInit.CONFIG.biome_overheat_temp && playerPositionHeight >= 0 && playerPositionHeight < playerEntity.world.getDimension().height() / 0.9f
            // // && playerEntity.world.isSkyVisible(playerEntity.getBlockPos()) && isDay) || playerEntity.world.getDimension().ultrawarm()) {
            // // thermometerXPosition = 96;
            // // } else {
            // // thermometerXPosition = 80;
            // // if (biomeTemperature <= ConfigInit.CONFIG.biome_cold_temp) {
            // // thermometerYPosition = 18;
            // // if (playerPositionHeight > playerEntity.world.getDimension().height() / 0.9f) {
            // // thermometerXPosition = 112;
            // // }
            // // } else if (biomeTemperature >= ConfigInit.CONFIG.biome_hot_temp && isDay && playerEntity.world.isSkyVisible(playerEntity.getBlockPos())) {
            // // thermometerYPosition = 0;
            // // } else {
            // // thermometerYPosition = 11;
            // // }
            // // }
            // }

            // if (temperatureManager.getThermometerCalm() > 0)

            // {
            // TemperatureHudRendering.renderThermometerIcon(matrixStack, client, playerEntity, 80, 11, scaledWidth, scaledHeight);
            // temperatureManager.setThermometerCalm(temperatureManager.getThermometerCalm() - 1);
            // } else {
            // TemperatureHudRendering.renderThermometerIcon(matrixStack, client, playerEntity, thermometerXPosition, thermometerYPosition, scaledWidth, scaledHeight);
            // }
            // }
        } else if ((playerEntity.isCreative() || playerEntity.isSpectator()) && RenderInit.getBlurProgress() > 0.001F) {
            RenderInit.setBlurProgress(0.00F);
        }
    }

}