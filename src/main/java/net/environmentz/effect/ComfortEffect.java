package net.environmentz.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ComfortEffect extends StatusEffect {

    public ComfortEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    // @Override
    // public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    //     if (entity instanceof PlayerEntity) {
    //         TemperatureManager temperatureManager = ((TemperatureManagerAccess) (PlayerEntity) entity).getTemperatureManager();

    //         int coldProtectionAmount = temperatureManager.getPlayerColdProtectionAmount();
    //         if (coldProtectionAmount < ConfigInit.CONFIG.max_cold_protection_amount)
    //             temperatureManager.setPlayerColdProtectionAmount(coldProtectionAmount + ConfigInit.CONFIG.cold_protection_amount_addition);
    //         int playerTemperature = temperatureManager.getPlayerTemperature();
    //         if (playerTemperature < -100)
    //             temperatureManager.setPlayerTemperature(playerTemperature + ConfigInit.CONFIG.cold_protection_amount_addition);
    //     }
    // }

    // @Override
    // public boolean canApplyUpdateEffect(int duration, int amplifier) {
    //     int i = 50 >> amplifier;
    //     if (i > 0) {
    //         return duration % i == 0;
    //     }
    //     return true;
    // }

}
