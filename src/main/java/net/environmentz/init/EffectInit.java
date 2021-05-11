package net.environmentz.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.environmentz.effect.*;

public class EffectInit {
  public final static StatusEffect COLDNESS = new ColdEffect(StatusEffectType.HARMFUL, 13553358);
  public final static StatusEffect WARMING = new WarmEffect(StatusEffectType.BENEFICIAL, 16771455);
  public final static StatusEffect OVERHEATING = new OverheatingEffect(StatusEffectType.HARMFUL, 16755263);
  public final static StatusEffect COOLING = new CoolEffect(StatusEffectType.BENEFICIAL, 6541055);

  public static void init() {
    Registry.register(Registry.STATUS_EFFECT, new Identifier("environmentz", "coldness"), COLDNESS);
    Registry.register(Registry.STATUS_EFFECT, new Identifier("environmentz", "warming"), WARMING);
    Registry.register(Registry.STATUS_EFFECT, new Identifier("environmentz", "overheating"), OVERHEATING);
    Registry.register(Registry.STATUS_EFFECT, new Identifier("environmentz", "cooling"), COOLING);
  }

}
