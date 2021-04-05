package net.environmentz.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.effect.ColdEffect;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
  private static final Identifier WINTER_TEX = new Identifier("environmentz:textures/misc/coldness_overlay.png");
  private static float smoothFreezingRendering;
  private static int ticker;

  @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableAlphaTest()V"))
  private static void renderOverlaysMixin(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
    PlayerEntity playerEntity = minecraftClient.player;
    if (!playerEntity.isCreative() && !playerEntity.isSpectator()) {
      ticker++;
      if (ticker >= 10) {
        if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() <= 0.0F
            && !ColdEffect.isWarmBlockNearBy(playerEntity)) {
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
      if (smoothFreezingRendering > 0.0F) {
        renderWinterOverlay(minecraftClient, matrixStack, smoothFreezingRendering);
      }
    }
  }

  private static void renderWinterOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, float smooth) {
    RenderSystem.enableTexture();
    minecraftClient.getTextureManager().bindTexture(WINTER_TEX);
    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
    float f = minecraftClient.player.getBrightnessAtEyes();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    float m = -minecraftClient.player.yaw / 64.0F;
    float n = minecraftClient.player.pitch / 64.0F;
    Matrix4f matrix4f = matrixStack.peek().getModel();
    bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
    bufferBuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, smooth).texture(4.0F + m, 4.0F + n).next();
    bufferBuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, smooth).texture(0.0F + m, 4.0F + n).next();
    bufferBuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, smooth).texture(0.0F + m, 0.0F + n).next();
    bufferBuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, smooth).texture(4.0F + m, 0.0F + n).next();
    bufferBuilder.end();
    BufferRenderer.draw(bufferBuilder);
    RenderSystem.disableBlend();
  }

}