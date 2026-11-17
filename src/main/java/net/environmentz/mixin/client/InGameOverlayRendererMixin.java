package net.environmentz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @Unique
    private static final ResourceLocation COLDNESS_OVERLAY = ResourceLocation.parse("environmentz:textures/gui/coldness_overlay.png");
    @Unique
    private static float smoothFreezingRendering;
    @Unique
    private static final int FADE_INTERVAL_TICKS = 5;
    @Unique
    private static long lastFadeTime;

    @Inject(method = "renderScreenEffect", at = @At(value = "TAIL"))
    private static void renderOverlaysMixin(Minecraft minecraftClient, PoseStack matrixStack, CallbackInfo info) {
        Player playerEntity = minecraftClient.player;
        if (playerEntity != null && !playerEntity.isCreative() && !playerEntity.isSpectator() && ConfigInit.CONFIG.coldOverlay) {
            long worldTime = playerEntity.level().getGameTime();
            if (worldTime < lastFadeTime || worldTime - lastFadeTime >= FADE_INTERVAL_TICKS) {
                lastFadeTime = worldTime;
                if (playerEntity.level().getBiome(playerEntity.blockPosition()).value().getBaseTemperature() <= Temperatures.getBiomeTemperature().veryCold()
                        && ((TemperatureManagerAccess) playerEntity).getTemperatureManager().getPlayerColdProtectionAmount() <= 0) {

                    float maxWhitening = 0.3F;
                    if (playerEntity.level().isRaining()) {
                        maxWhitening = 0.5F;
                    }
                    if (smoothFreezingRendering < maxWhitening) {
                        smoothFreezingRendering = smoothFreezingRendering + 0.02F;
                    }
                } else if (smoothFreezingRendering > 0.0F) {
                    smoothFreezingRendering = smoothFreezingRendering - 0.02F;
                }
            }
            if (smoothFreezingRendering > 0.01F) {
                renderWinterOverlay(minecraftClient, matrixStack, smoothFreezingRendering);
            }
        }
    }

    private static void renderWinterOverlay(Minecraft client, PoseStack matrices, float smooth) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, COLDNESS_OVERLAY);
        BlockPos blockPos = BlockPos.containing(client.player.getX(), client.player.getEyeY(), client.player.getZ());
        float f = LightTexture.getBrightness(client.player.level().dimensionType(), client.player.level().getMaxLocalRawBrightness(blockPos));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(f, f, f, smooth);

        float m = -client.player.getYRot() / 64.0f;
        float n = client.player.getXRot() / 64.0f;
        Matrix4f matrix4f = matrices.last().pose();
        BufferBuilder bufferBuilder =  bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -1.0f, -1.0f, -0.5f).setUv(4.0f + m, 4.0f + n);
        bufferBuilder.addVertex(matrix4f, 1.0f, -1.0f, -0.5f).setUv(0.0f + m, 4.0f + n);
        bufferBuilder.addVertex(matrix4f, 1.0f, 1.0f, -0.5f).setUv(0.0f + m, 0.0f + n);
        bufferBuilder.addVertex(matrix4f, -1.0f, 1.0f, -0.5f).setUv(4.0f + m, 0.0f + n);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

}