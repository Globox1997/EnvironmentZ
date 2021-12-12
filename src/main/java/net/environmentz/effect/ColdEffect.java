// package net.environmentz.effect;

// import java.util.UUID;

// import net.environmentz.init.ConfigInit;
// import net.environmentz.init.EffectInit;
// import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.attribute.AttributeContainer;
// import net.minecraft.entity.attribute.EntityAttributeInstance;
// import net.minecraft.entity.attribute.EntityAttributeModifier;
// import net.minecraft.entity.attribute.EntityAttributes;
// import net.minecraft.entity.damage.DamageSource;
// import net.minecraft.entity.effect.StatusEffect;
// import net.minecraft.entity.effect.StatusEffectCategory;
// import net.minecraft.entity.player.PlayerEntity;

// public class ColdEffect extends StatusEffect {
// private static final UUID COLDNESS = UUID.fromString("a8287185-47ef-4b9f-a6d3-643b5833181d");

// public ColdEffect(StatusEffectCategory type, int color) {
// super(type, color);
// }

// @Override
// public void applyUpdateEffect(LivingEntity entity, int amplifier) {
// // if (!entity.world.isClient && entity.hasStatusEffect(EffectInit.WARMING)) {
// // float wetMultiplicator = 1.0F;
// // if (entity.hasStatusEffect(EffectInit.WET)) {
// // wetMultiplicator = 2.0F;
// // }
// // // entity.damage(COLD, ConfigInit.CONFIG.cold_damage * wetMultiplicator);
// // if (entity instanceof PlayerEntity) {
// // ((PlayerEntity) entity).addExhaustion(0.005F);
// // }
// // }
// }

// @Override
// public boolean canApplyUpdateEffect(int duration, int amplifier) {
// return false;
// // return duration % ConfigInit.CONFIG.cold_damage_interval == 0;
// }

// @Override
// public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
// EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance((EntityAttributes.GENERIC_MOVEMENT_SPEED));
// if (entityAttributeInstance != null) {
// EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(this.getTranslationKey(), -0.15D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
// entityAttributeInstance.removeModifier(entityAttributeModifier);
// entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(COLDNESS, this.getTranslationKey() + " " + entityAttributeModifier.getValue(),
// this.adjustModifierAmount(amplifier, entityAttributeModifier), entityAttributeModifier.getOperation()));
// }

// }

// @Override
// public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
// EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
// if (entityAttributeInstance != null) {
// entityAttributeInstance.removeModifier(COLDNESS);
// }

// }

// }
