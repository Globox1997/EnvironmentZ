package net.environmentz.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.environmentz.effect.*;

public class EffectInit {
    public final static StatusEffect WARMING = new WarmEffect(StatusEffectCategory.BENEFICIAL, 16771455);
    public final static StatusEffect COOLING = new CoolEffect(StatusEffectCategory.BENEFICIAL, 6541055);
    public final static StatusEffect COMFORT = new ComfortEffect(StatusEffectCategory.BENEFICIAL, 0xE8732D);

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier("environmentz", "warming"), WARMING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("environmentz", "cooling"), COOLING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier("environmentz", "comfort"), COMFORT);
    }

}
