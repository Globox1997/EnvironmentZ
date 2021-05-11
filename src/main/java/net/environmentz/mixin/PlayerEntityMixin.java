package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.util.TemperatureAspects;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.EffectInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
  private int ticker;

  public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void tickMixin(CallbackInfo info) {
    PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    if (!this.world.isClient && !playerEntity.isCreative() && !playerEntity.isSpectator()) {
      ticker++;
      if (ticker >= 20) {
        if (this.world.getBiome(this.getBlockPos()).getTemperature() <= ConfigInit.CONFIG.biome_freeze_temp) {
          TemperatureAspects.coldEnvironment(playerEntity);
          if (TemperatureAspects.dehydrationTimer > 0) {
            TemperatureAspects.dehydrationTimer = 0;
          }
        } else if (this.world.getBiome(this.getBlockPos()).getTemperature() >= ConfigInit.CONFIG.biome_overheat_temp) {
          TemperatureAspects.hotEnvironment(playerEntity);
          if (TemperatureAspects.coldnessTimer > 0) {
            TemperatureAspects.coldnessTimer = 0;
          }
        }
        if (this.hasStatusEffect(EffectInit.COLDNESS) || this.hasStatusEffect(EffectInit.OVERHEATING)) {
          TemperatureAspects.acclimatize(playerEntity);
        } else if (TemperatureAspects.acclimatizeTimer > 0) {
          TemperatureAspects.acclimatizeTimer = 0;
        }
        ticker = 0;
      }
    }
  }
}