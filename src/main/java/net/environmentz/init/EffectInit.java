package net.environmentz.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.environmentz.effect.*;

public class EffectInit {
    public final static StatusEffect WARMING = new WarmEffect(StatusEffectCategory.BENEFICIAL, 16771455);
    public final static StatusEffect COOLING = new CoolEffect(StatusEffectCategory.BENEFICIAL, 6541055);

    public static void init() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier("environmentz", "warming"), WARMING);
        Registry.register(Registry.STATUS_EFFECT, new Identifier("environmentz", "cooling"), COOLING);
    }

}
