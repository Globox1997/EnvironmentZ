package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.environmentz.effect.ComfortEffect;
import net.environmentz.effect.CoolEffect;
import net.environmentz.effect.WarmEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectInit {
    public final static Holder<MobEffect> WARMING = register("warming", new WarmEffect(MobEffectCategory.BENEFICIAL, 16771455));
    public final static Holder<MobEffect> COOLING = register("cooling", new CoolEffect(MobEffectCategory.BENEFICIAL, 6541055));
    public final static Holder<MobEffect> COMFORT = register("comfort", new ComfortEffect(MobEffectCategory.BENEFICIAL, 0xE8732D));

    public static void init() {
    }

    private static Holder<MobEffect> register(String id, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, EnvironmentzMain.identifierOf(id), statusEffect);
    }

}
