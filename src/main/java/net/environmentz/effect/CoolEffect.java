package net.environmentz.effect;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.init.ConfigInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class CoolEffect extends StatusEffect {

    public CoolEffect(StatusEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            int hotProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerHeatProtectionAmount();
            if (hotProtectionAmount < ConfigInit.CONFIG.max_heat_protection_amount)
                ((PlayerEnvAccess) playerEntity).setPlayerHeatProtectionAmount(hotProtectionAmount + ConfigInit.CONFIG.heat_protection_amount_addition);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 50 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        }
        return true;
    }

}
