// package net.environmentz.effect;

// import java.util.UUID;

// import net.dehydration.access.ThirstManagerAccess;
// import net.environmentz.init.ConfigInit;
// import net.fabricmc.loader.api.FabricLoader;
// import net.minecraft.entity.LivingEntity;
// import net.minecraft.entity.attribute.AttributeContainer;
// import net.minecraft.entity.attribute.EntityAttributeInstance;
// import net.minecraft.entity.attribute.EntityAttributeModifier;
// import net.minecraft.entity.attribute.EntityAttributes;
// import net.minecraft.entity.effect.StatusEffect;
// import net.minecraft.entity.effect.StatusEffectCategory;
// import net.minecraft.entity.player.PlayerEntity;

// public class OverheatingEffect extends StatusEffect {
// private final UUID DEHYDRATION = UUID.fromString("80e24bea-844e-4944-a36a-edb66e841e66");

// public OverheatingEffect(StatusEffectCategory type, int color) {
// super(type, color);
// }

// @Override
// public void applyUpdateEffect(LivingEntity entity, int amplifier) {
// if (entity instanceof PlayerEntity && FabricLoader.getInstance().isModLoaded("dehydration")) {
// PlayerEntity player = (PlayerEntity) entity;
// ((ThirstManagerAccess) player).getThirstManager(player).addDehydration(0.5F + (float) amplifier);
// }
// }

// @Override
// public boolean canApplyUpdateEffect(int duration, int amplifier) {
// return false;
// // return duration % ConfigInit.CONFIG.overheating_damage_interval == 0;
// }

// @Override
// public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
// EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance((EntityAttributes.GENERIC_MOVEMENT_SPEED));
// if (entityAttributeInstance != null) {
// EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(this.getTranslationKey(), -0.15D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
// entityAttributeInstance.removeModifier(entityAttributeModifier);
// entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(DEHYDRATION, this.getTranslationKey() + " " + entityAttributeModifier.getValue(),
// this.adjustModifierAmount(0, entityAttributeModifier), entityAttributeModifier.getOperation()));
// }

// }

// @Override
// public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
// EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
// if (entityAttributeInstance != null) {
// entityAttributeInstance.removeModifier(DEHYDRATION);
// }

// }

// }