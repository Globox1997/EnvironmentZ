package net.environmentz.init;

import java.util.function.BiConsumer;

import net.environmentz.EnvironmentzMain;
import net.environmentz.effect.ComfortEffect;
import net.environmentz.effect.CoolEffect;
import net.environmentz.effect.WarmEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EffectInit {

    public static Holder<MobEffect> WARMING;
    public static Holder<MobEffect> COOLING;
    public static Holder<MobEffect> COMFORT;

    public static void register(BiConsumer<Identifier, MobEffect> registrar) {
        WARMING = register(registrar, "warming", new WarmEffect(MobEffectCategory.BENEFICIAL, 16771455));
        COOLING = register(registrar, "cooling", new CoolEffect(MobEffectCategory.BENEFICIAL, 6541055));
        COMFORT = register(registrar, "comfort", new ComfortEffect(MobEffectCategory.BENEFICIAL, 0xE8732D));
    }

    private static Holder<MobEffect> register(BiConsumer<Identifier, MobEffect> registrar, String id, MobEffect statusEffect) {
        registrar.accept(EnvironmentzMain.identifierOf(id), statusEffect);
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(statusEffect);
    }
}
