package net.environmentz.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.data.BodyTemperature;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
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
    MinecraftClient client;

    @Shadow
    private int ticks;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0))
    private void renderWorldMixin(RenderTickCounter tickCounter, CallbackInfo info, @Local(ordinal = 0) Matrix4f matrix4f) {
        ClientPlayerEntity player = this.client.player;
        if (!ConfigInit.CONFIG.shakingScreenEffect || player == null || player.isCreative() || player.isSpectator()) {
            return;
        }
        int playerTemperature = ((TemperatureManagerAccess) player).getTemperatureManager().getPlayerTemperature();
        BodyTemperature bodyTemperature = Temperatures.getBodyTemperature();
        if (playerTemperature > bodyTemperature.maxCold()) {
            return;
        }
        int period = Math.abs(bodyTemperature.maxVeryCold() * 2) + playerTemperature * 2;
        if (period <= 0 || this.ticks % period < period / 2) {
            matrix4f.translate((float) Math.cos(this.ticks * Math.PI) * 0.01f * this.client.options.getDistortionEffectScale().getValue().floatValue(), 0.0f, 0.0f);
        }
    }
}
