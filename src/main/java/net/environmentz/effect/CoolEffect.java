package net.environmentz.effect;

import net.environmentz.access.PlayerEnvAccess;
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
            if (hotProtectionAmount < 120)
                ((PlayerEnvAccess) playerEntity).setPlayerHeatProtectionAmount(hotProtectionAmount + 2);
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
