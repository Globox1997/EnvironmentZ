package net.environmentz.mixin.client;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Mutable
    @Final
    @Shadow
    MinecraftClient client;

    @Shadow
    private int ticks;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderWorldMixin(RenderTickCounter tickCounter, CallbackInfo info, float f, boolean bl, Camera camera, Entity entity, float g, double d, Matrix4f matrix4f, MatrixStack matrixStack) {
        int playerTemperature = ((TemperatureManagerAccess) client.player).getTemperatureManager().getPlayerTemperature();
        // Needs further tweak
        if (ConfigInit.CONFIG.shakingScreenEffect && playerTemperature <= Temperatures.getBodyTemperatures(1) && !this.client.player.isCreative() && !this.client.player.isSpectator()) {
            if (playerTemperature == Temperatures.getBodyTemperatures(0) || this.ticks
                    % (Math.abs(Temperatures.getBodyTemperatures(0) * 2) + playerTemperature * 2) < (((Math.abs(Temperatures.getBodyTemperatures(0) * 2) + playerTemperature * 2)) / 2)) {
                matrix4f.translate((float) (Math.cos((double) this.ticks * Math.PI)) * 0.01f * this.client.options.getDistortionEffectScale().getValue().floatValue(), 0.0f, 0.0f);
            }
        }
    }
}
