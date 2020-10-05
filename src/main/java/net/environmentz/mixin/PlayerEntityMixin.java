package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.effect.ColdEffect;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.EffectInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
  private static int coldnessTimer;
  private static int warmingTimer;
  private static int coldTickInterval = ConfigInit.CONFIG.cold_tick_interval;
  private static int heatingUpColdTickDecrease = ConfigInit.CONFIG.heating_up_cold_tick_decrease;
  private static int heatingUpInterval = ConfigInit.CONFIG.heating_up_interval;
  private static int coldDamageEffectTime = ConfigInit.CONFIG.cold_damage_effect_time;
  private static int warmArmorTickModifier = ConfigInit.CONFIG.warm_armor_tick_modifier;

  public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void tickMixin(CallbackInfo info) {
    PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    if (!playerEntity.isCreative()) {
      if (this.world.getBiome(this.getBlockPos()).getTemperature() <= 0.0F && !ColdEffect.isWarmBlockNearBy(this)) {
        if (ColdEffect.warmClothingModifier(this) != (warmArmorTickModifier * 4)) {
          coldnessTimer++;
          if (coldnessTimer >= (coldTickInterval + ColdEffect.warmClothingModifier(this))) {
            this.addStatusEffect(
                new StatusEffectInstance(EffectInit.COLDNESS, coldDamageEffectTime, 0, false, false, true));
            coldnessTimer = 0;
          }
        }
      } else if (coldnessTimer > 0) {
        coldnessTimer = 0;
      }
      if (this.hasStatusEffect(EffectInit.COLDNESS)) {
        if (ColdEffect.isWarmBlockNearBy(this) || this.world.getBiome(this.getBlockPos()).getTemperature() >= 2.0F) {
          warmingTimer++;
          if (warmingTimer >= heatingUpInterval) {
            int coldDuration = this.getStatusEffect(EffectInit.COLDNESS).getDuration();
            this.removeStatusEffect(EffectInit.COLDNESS);
            if (coldDuration > heatingUpColdTickDecrease) {
              this.addStatusEffect(new StatusEffectInstance(EffectInit.COLDNESS,
                  coldDuration - heatingUpColdTickDecrease, 0, false, false, true));
            }
            warmingTimer = 0;
          }
        }
      }
    }
  }
}
