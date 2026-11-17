package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.environmentz.init.ItemInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void canWalkOnPowderSnowMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof LivingEntity && (((LivingEntity) entity).getItemBySlot(EquipmentSlot.FEET).is(ItemInit.WOLF_BOOTS) || ((LivingEntity) entity).getItemBySlot(EquipmentSlot.FEET).is(ItemInit.WOOL_BOOTS))) {
            info.setReturnValue(true);
        }
    }
}
