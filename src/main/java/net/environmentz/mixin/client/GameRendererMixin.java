package net.environmentz.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.TemperatureBlurRendering;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.data.BodyTemperature;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Final
    @Shadow
    Minecraft minecraft;

    @Shadow
    private int confusionAnimationTick;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private void renderWorldMixin(DeltaTracker tickCounter, CallbackInfo info, @Local(ordinal = 0) Matrix4f matrix4f) {
        LocalPlayer player = this.minecraft.player;
        if (!ConfigInit.CONFIG.shakingScreenEffect || player == null || player.isCreative() || player.isSpectator()) {
            return;
        }
        int playerTemperature = ((TemperatureManagerAccess) player).getTemperatureManager().getPlayerTemperature();
        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        if (playerTemperature > bodyTemperature.maxCold()) {
            return;
        }
        int period = Math.abs(bodyTemperature.maxVeryCold() * 2) + playerTemperature * 2;
        if (period <= 0 || this.confusionAnimationTick % period < period / 2) {
            matrix4f.translate((float) Math.cos(this.confusionAnimationTick * Math.PI) * 0.01f * this.minecraft.options.screenEffectScale().get().floatValue(), 0.0f, 0.0f);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V"))
    private void renderBlurMixin(DeltaTracker tickCounter, boolean tick, CallbackInfo info) {
        TemperatureBlurRendering.render(this.minecraft, tickCounter.getGameTimeDeltaTicks());
    }
}
