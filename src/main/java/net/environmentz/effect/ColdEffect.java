package net.environmentz.effect;

import java.util.UUID;

import net.environmentz.init.ConfigInit;
import net.environmentz.init.TagInit;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class ColdEffect extends StatusEffect {
  private static final UUID COLDNESS = UUID.fromString("a8287185-47ef-4b9f-a6d3-643b5833181d");

  public ColdEffect(StatusEffectType type, int color) {
    super(type, color);
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    if (!isWarmBlockNearBy(entity)) {
      DamageSource damageSource = createDamageSource();
      entity.damage(damageSource, ConfigInit.CONFIG.cold_damage);
      ((PlayerEntity) entity).addExhaustion(0.005F);
    }
  }

  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return duration % ConfigInit.CONFIG.cold_damage_interval == 0;
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
          new EntityAttributeModifier(COLDNESS, this.getTranslationKey() + " " + entityAttributeModifier.getValue(),
              this.adjustModifierAmount(amplifier, entityAttributeModifier), entityAttributeModifier.getOperation()));
    }

  }

  @Override
  public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
    EntityAttributeInstance entityAttributeInstance = attributes
        .getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    if (entityAttributeInstance != null) {
      entityAttributeInstance.removeModifier(COLDNESS);
    }

  }

  public static DamageSource createDamageSource() {
    return new EntityDamageSource("cold", null);
  }

  public static boolean hasWarmClothing(LivingEntity livingEntity) {
    if ((livingEntity.getEquippedStack(EquipmentSlot.HEAD).isItemEqualIgnoreDamage(new ItemStack(Items.LEATHER_HELMET))
        && livingEntity.getEquippedStack(EquipmentSlot.CHEST)
            .isItemEqualIgnoreDamage(new ItemStack(Items.LEATHER_CHESTPLATE))
        && livingEntity.getEquippedStack(EquipmentSlot.LEGS)
            .isItemEqualIgnoreDamage(new ItemStack(Items.LEATHER_LEGGINGS))
        && livingEntity.getEquippedStack(EquipmentSlot.FEET)
            .isItemEqualIgnoreDamage(new ItemStack(Items.LEATHER_BOOTS)))
        || (ConfigInit.CONFIG.allow_all_armor
            && (livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem().isIn(TagInit.WARM_ARMOR)
                && livingEntity.getEquippedStack(EquipmentSlot.CHEST).getItem().isIn(TagInit.WARM_ARMOR)
                && livingEntity.getEquippedStack(EquipmentSlot.LEGS).getItem().isIn(TagInit.WARM_ARMOR)
                && livingEntity.getEquippedStack(EquipmentSlot.FEET).getItem().isIn(TagInit.WARM_ARMOR)))) {
      return true;
    } else
      return false;
  }

  public static boolean isWarmBlockNearBy(LivingEntity livingEntity) {
    for (int i = -1; i < 2; i++) {
      for (int u = -1; u < 2; u++) {
        BlockPos pos = new BlockPos(livingEntity.getBlockPos().getX() + i, livingEntity.getBlockPos().getY(),
            livingEntity.getBlockPos().getZ() + u);
        if (livingEntity.world.getBlockState(pos).isIn(TagInit.WARMING_BLOCKS)) {
          return true;
        }
      }
    }
    return false;
  }

}
