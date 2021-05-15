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
import net.environmentz.effect.OverheatingEffect;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.EffectInit;
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

  private static final Identifier FREEZING_ICON = new Identifier("environmentz:textures/gui/coldness.png");
  private static final Identifier OVERHEATING_ICON = new Identifier("environmentz:textures/gui/overheating.png");

  private float smoothFreezingIconRendering;
  private float smoothThirstRendering;
  private int ticker;
  private boolean isInColdBiome;
  private boolean isInHotBiome;

  public InGameHudMixin(MinecraftClient client) {
    this.client = client;
  }

  @Inject(method = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"))
  private void renderFreezingIconMixin(MatrixStack matrixStack, float f, CallbackInfo info) {
    // Gets ticked 60 times per second
    PlayerEntity playerEntity = client.player;
    if (!playerEntity.isCreative() && !playerEntity.isSpectator()) {
      ticker++;
      if (ticker >= 20) {
        if (playerEntity.world.getBiome(playerEntity.getBlockPos())
            .getTemperature() <= ConfigInit.CONFIG.biome_freeze_temp) {
          isInColdBiome = true;
          if (!playerEntity.hasStatusEffect(EffectInit.WARMING)
              && ColdEffect.warmClothingModifier(playerEntity) != (ConfigInit.CONFIG.warm_armor_tick_modifier * 4)
              && !ColdEffect.isWarmBlockNearBy(playerEntity)) {
            int wetMalus = 0;
            if (playerEntity.hasStatusEffect(EffectInit.WET)) {
              wetMalus = ConfigInit.CONFIG.wet_bonus_malus;
            }
            if (smoothFreezingIconRendering < 1.0F) {
              smoothFreezingIconRendering = smoothFreezingIconRendering
                  + (1.0F / ((float) (ConfigInit.CONFIG.cold_tick_interval + ConfigInit.CONFIG.warm_armor_tick_modifier
                      - wetMalus)) / 2.8F);
            }
            if (smoothFreezingIconRendering > 1.0F) {
              smoothFreezingIconRendering = 1.0F;
            }
          } else if (smoothFreezingIconRendering > 0.0F) {
            smoothFreezingIconRendering = smoothFreezingIconRendering
                - (1.0F / ((float) ConfigInit.CONFIG.heating_up_interval) / 2.8F);
          }
        } else if (isInColdBiome) {
          isInColdBiome = false;
          smoothFreezingIconRendering = 0.0F;

        } else if (playerEntity.world.getBiome(playerEntity.getBlockPos())
            .getTemperature() >= ConfigInit.CONFIG.biome_overheat_temp) {
          isInHotBiome = true;
          if (OverheatingEffect.wearsArmor(playerEntity) && playerEntity.world.isSkyVisible(playerEntity.getBlockPos())
              && ((int) playerEntity.world.getTimeOfDay() > 23000 || (int) playerEntity.world.getTimeOfDay() < 12000)) {
            int wetBonus = 0;
            if (playerEntity.hasStatusEffect(EffectInit.WET)) {
              wetBonus = ConfigInit.CONFIG.wet_bonus_malus;
            }
            if (smoothThirstRendering < 1.0F) {
              smoothThirstRendering = smoothThirstRendering
                  + (1.0F / ((float) (ConfigInit.CONFIG.overheating_tick_interval + wetBonus)) / 2.8F);
            }
            if (smoothThirstRendering > 1.0F) {
              smoothThirstRendering = 1.0F;
            }
          } else if (smoothThirstRendering > 0.0F) {
            smoothThirstRendering = smoothThirstRendering - 0.01F;
          }
        } else if (isInHotBiome) {
          isInHotBiome = false;
          smoothThirstRendering = 0.0F;
        }
        // Ticker zeroing
        ticker = 0;
      }
      if (!ConfigInit.CONFIG.excluded_cold_names.contains(playerEntity.getName().asString())
          && (isInColdBiome && smoothFreezingIconRendering > 0.0F)) {
        this.renderIconBackgroundOverlay(matrixStack, FREEZING_ICON);
        if (smoothFreezingIconRendering > 0.0F) {
          this.renderIconOverlay(matrixStack, smoothFreezingIconRendering, FREEZING_ICON);
        }
      } else if (!ConfigInit.CONFIG.excluded_heat_names.contains(playerEntity.getName().asString())
          && (isInHotBiome && smoothThirstRendering > 0.0F)) {
        this.renderIconBackgroundOverlay(matrixStack, OVERHEATING_ICON);
        if (smoothThirstRendering > 0.0F) {
          this.renderIconOverlay(matrixStack, smoothThirstRendering, OVERHEATING_ICON);
        }
      }
    }
  }

  private void renderIconOverlay(MatrixStack matrixStack, float smooth, Identifier identifier) {
    int scaledWidth = this.client.getWindow().getScaledWidth();
    int scaledHeight = this.client.getWindow().getScaledHeight();
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.client.getTextureManager().bindTexture(identifier);
    DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x,
        scaledHeight - ConfigInit.CONFIG.icon_y + (13 - this.iconTexture(smooth)), 13.0F,
        13.0F - (this.iconTexture(smooth) - 13), 13, this.iconTexture(smooth), 26, 13);
  }

  private void renderIconBackgroundOverlay(MatrixStack matrixStack, Identifier identifier) {
    int scaledWidth = this.client.getWindow().getScaledWidth();
    int scaledHeight = this.client.getWindow().getScaledHeight();
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.client.getTextureManager().bindTexture(identifier);
    DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2) - ConfigInit.CONFIG.icon_x,
        scaledHeight - ConfigInit.CONFIG.icon_y, 0.0F, 0.0F, 13, 13, 26, 13);
  }

  private int iconTexture(float smooth) {
    if (smooth >= 0.85F) {
      return 13;
    } else if (smooth > 0.68F) {
      return 10;
    } else if (smooth > 0.51F) {
      return 8;
    } else if (smooth > 0.34F) {
      return 5;
    } else if (smooth > 0.17F) {
      return 3;
    } else
      return 0;
  }

}