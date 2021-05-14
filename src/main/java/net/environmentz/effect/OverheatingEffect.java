package net.environmentz.effect;

import java.util.UUID;

import net.dehydration.access.ThristManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class OverheatingEffect extends StatusEffect {
  private final UUID DEHYDRATION = UUID.fromString("80e24bea-844e-4944-a36a-edb66e841e66");

  public OverheatingEffect(StatusEffectType type, int color) {
    super(type, color);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (entity instanceof PlayerEntity && FabricLoader.getInstance().isModLoaded("dehydration")) {
      PlayerEntity player = (PlayerEntity) entity;
      ((ThristManagerAccess) player).getThirstManager(player).addDehydration(0.5F + (float) amplifier);
    }
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return duration % ConfigInit.CONFIG.overheating_damage_interval == 0;
  }

  @Override
  public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    EntityAttributeInstance entityAttributeInstance = attributes
        .getCustomInstance((EntityAttributes.GENERIC_MOVEMENT_SPEED));
    if (entityAttributeInstance != null) {
      EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(this.getTranslationKey(), -0.15D,
          EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
      entityAttributeInstance.removeModifier(entityAttributeModifier);
      entityAttributeInstance.addPersistentModifier(
          new EntityAttributeModifier(DEHYDRATION, this.getTranslationKey() + " " + entityAttributeModifier.getValue(),
              this.adjustModifierAmount(0, entityAttributeModifier), entityAttributeModifier.getOperation()));
    }

  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    EntityAttributeInstance entityAttributeInstance = attributes
        .getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    if (entityAttributeInstance != null) {
      entityAttributeInstance.removeModifier(DEHYDRATION);
    }

  }

  public static boolean wearsArmor(LivingEntity livingEntity) {
    boolean wearsArmor = true;
    boolean armorDebuff = ConfigInit.CONFIG.disable_armor_debuff;
    ItemStack headStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
    ItemStack chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
    ItemStack legStack = livingEntity.getEquippedStack(EquipmentSlot.LEGS);
    ItemStack feetStack = livingEntity.getEquippedStack(EquipmentSlot.FEET);

    if (!armorDebuff) {
      if (headStack.isEmpty() || headStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        wearsArmor = false;
      }
      if (chestStack.isEmpty() || chestStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        wearsArmor = false;
      }
      if (legStack.isEmpty() || legStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        wearsArmor = false;
      }
      if (feetStack.isEmpty() || feetStack.getItem().isIn(TagInit.ALLOWED_ARMOR)) {
        wearsArmor = false;
      }
      return wearsArmor;
    } else
      return false;
  }

}
