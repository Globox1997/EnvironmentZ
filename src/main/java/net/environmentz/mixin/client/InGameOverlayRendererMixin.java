package net.environmentz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    private static final Identifier COLDNESS_OVERLAY = new Identifier("environmentz:textures/gui/coldness_overlay.png");
    private static float smoothFreezingRendering;
    private static int ticker;

    @Inject(method = "renderOverlays", at = @At(value = "TAIL"))
    private static void renderOverlaysMixin(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
        PlayerEntity playerEntity = minecraftClient.player;
        if (!playerEntity.isCreative() && !playerEntity.isSpectator() && ConfigInit.CONFIG.cold_overlay) {
            ticker++;
            if (ticker >= 20) {
                if (playerEntity.world.getBiome(playerEntity.getBlockPos()).value().getTemperature() <= Temperatures.getBiomeTemperatures(0)
                        && ((TemperatureManagerAccess) playerEntity).getTemperatureManager().getPlayerColdProtectionAmount() <= 0) {

                    float maxWhitening = 0.3F;
                    if (playerEntity.world.isRaining()) {
                        maxWhitening = 0.5F;
                    }
                    if (smoothFreezingRendering < maxWhitening) {
                        smoothFreezingRendering = smoothFreezingRendering + 0.02F;
                    }
                } else if (smoothFreezingRendering > 0.0F) {
                    smoothFreezingRendering = smoothFreezingRendering - 0.02F;
                }
                ticker = 0;
            }
            if (smoothFreezingRendering > 0.01F) {
                renderWinterOverlay(minecraftClient, matrixStack, smoothFreezingRendering);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void renderWinterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, float smooth) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, COLDNESS_OVERLAY);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float f = minecraftClient.player.getBrightnessAtEyes();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(f, f, f, smooth);
        float m = -minecraftClient.player.getYaw() / 64.0F;
        float n = minecraftClient.player.getPitch() / 64.0F;
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).texture(4.0F + m, 4.0F + n).next();
        bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).texture(0.0F + m, 4.0F + n).next();
        bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).texture(0.0F + m, 0.0F + n).next();
        bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).texture(4.0F + m, 0.0F + n).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

}