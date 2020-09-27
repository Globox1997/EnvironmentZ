package net.environmentz.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.environmentz.effect.ColdEffect;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
  @Shadow
  @Final
  @Mutable
  private final MinecraftClient client;

  private static final Identifier FREEZING_ICON = new Identifier("environmentz:textures/mob_effect/coldness.png");
  private static float smoothRendering = 0.0F;

  public InGameHudMixin(MinecraftClient client) {
    this.client = client;
  }

  @Inject(method = "render", at = @At(value = "TAIL"))
  private void renderFreezingIcon(MatrixStack matrixStack, float f, CallbackInfo info) {
    PlayerEntity playerEntity = client.player;
    if (!playerEntity.isCreative()) {
      if (playerEntity.world.getBiome(playerEntity.getBlockPos()).getTemperature() <= 0.0F
          && !ColdEffect.hasWarmClothing(playerEntity) && !ColdEffect.isWarmBlockNearBy(playerEntity)) {
        if (smoothRendering < 0.98F) {
          smoothRendering = smoothRendering + 0.0025F;
        }
        this.renderFreezingIconOverlay(matrixStack, smoothRendering);
      } else if (smoothRendering > 0.0F) {
        smoothRendering = smoothRendering - 0.01F;
        this.renderFreezingIconOverlay(matrixStack, smoothRendering);
      }

    }
  }

  private void renderFreezingIconOverlay(MatrixStack matrixStack, float smooth) {
    int scaledWidth = this.client.getWindow().getScaledWidth();
    int scaledHeight = this.client.getWindow().getScaledHeight();
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, smooth);
    this.client.getTextureManager().bindTexture(FREEZING_ICON);
    DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - 9, scaledHeight - 49, 0.0F, 0.0F, 18, 18, 18, 18);
  }

}

// When you go into snowy biomes you'll start to freeze if you don't wear
// leather armor (configure able)

// If you are freezing, you can only walk slower and you'll take damage over
// time.

// Warm yourself up at a fireplace :)