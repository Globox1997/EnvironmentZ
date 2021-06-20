package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.environmentz.init.ConfigInit;
import net.environmentz.util.TemperatureHudRendering;
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

    private int ticker;
    private int wetTimer;

    public InGameHudMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
    private void renderMixin(MatrixStack matrixStack, float f, CallbackInfo info) {
        // Gets ticked 60 times per second
        PlayerEntity playerEntity = client.player;
        if (!playerEntity.isCreative() && !playerEntity.isSpectator() && !playerEntity.isInvulnerable()) {
            ticker++;
            if (ticker >= 60 && !client.isPaused()) {
                wetTimer++;
                if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() <= ConfigInit.CONFIG.biome_freeze_temp) {
                    TemperatureHudRendering.coldEnvRendering(playerEntity, wetTimer);
                } else if (TemperatureHudRendering.isInColdBiome) {
                    TemperatureHudRendering.isInColdBiome = false;
                    TemperatureHudRendering.smoothFreezingIconRendering = 0.0F;

                } else if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() >= ConfigInit.CONFIG.biome_overheat_temp) {
                    TemperatureHudRendering.hotEnvRendering(playerEntity, wetTimer);
                } else if (TemperatureHudRendering.isInHotBiome) {
                    TemperatureHudRendering.isInHotBiome = false;
                    TemperatureHudRendering.smoothThirstRendering = 0.0F;
                }
                // Ticker zeroing
                ticker = 0;
                if (wetTimer >= ConfigInit.CONFIG.wet_bonus_malus) {
                    wetTimer = 0;
                }
            }
            TemperatureHudRendering.renderTemperatureAspect(matrixStack, playerEntity, client);
        }
    }

}