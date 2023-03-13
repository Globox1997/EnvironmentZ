package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Mutable
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private int ticks;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderWorldMixin(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo info, boolean bl, Camera camera, MatrixStack matrixStack, double d, float f) {
        int playerTemperature = ((TemperatureManagerAccess) client.player).getTemperatureManager().getPlayerTemperature();
        // Needs further tweak
        if (ConfigInit.CONFIG.shaking_screen_effect && playerTemperature <= Temperatures.getBodyTemperatures(1) && !this.client.player.isCreative() && !this.client.player.isSpectator()) {
            if (playerTemperature == Temperatures.getBodyTemperatures(0) || this.ticks
                    % (Math.abs(Temperatures.getBodyTemperatures(0) * 2) + playerTemperature * 2) < (((Math.abs(Temperatures.getBodyTemperatures(0) * 2) + playerTemperature * 2)) / 2)) {
                Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                matrix4f.multiplyByTranslation((float) (Math.cos((double) this.ticks * Math.PI)) * 0.01f * this.client.options.getDistortionEffectScale().getValue().floatValue(), 0.0f, 0.0f);
            }
        }
    }
}
