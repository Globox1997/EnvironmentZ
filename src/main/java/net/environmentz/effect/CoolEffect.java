package net.environmentz.effect;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.TemperatureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings("unused")
public class CoolEffect extends StatusEffect {

    public CoolEffect(StatusEffectCategory type, int color) {
        super(type, color);
    }

}
