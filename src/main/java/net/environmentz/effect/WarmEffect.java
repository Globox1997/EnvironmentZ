package net.environmentz.effect;

import net.environmentz.access.PlayerEnvAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class WarmEffect extends StatusEffect {

    public WarmEffect(StatusEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            int coldProtectionAmount = ((PlayerEnvAccess) playerEntity).getPlayerColdProtectionAmount();
            if (coldProtectionAmount < 120)
                ((PlayerEnvAccess) playerEntity).setPlayerColdProtectionAmount(coldProtectionAmount + 2);
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
