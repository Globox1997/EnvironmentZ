package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.environmentz.effect.ComfortEffect;
import net.environmentz.effect.CoolEffect;
import net.environmentz.effect.WarmEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public class EffectInit {
    public final static RegistryEntry<StatusEffect> WARMING = register("warming", new WarmEffect(StatusEffectCategory.BENEFICIAL, 16771455));
    public final static RegistryEntry<StatusEffect> COOLING = register("cooling", new CoolEffect(StatusEffectCategory.BENEFICIAL, 6541055));
    public final static RegistryEntry<StatusEffect> COMFORT = register("comfort", new ComfortEffect(StatusEffectCategory.BENEFICIAL, 0xE8732D));

    public static void init() {
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, EnvironmentzMain.identifierOf(id), statusEffect);
    }

}
