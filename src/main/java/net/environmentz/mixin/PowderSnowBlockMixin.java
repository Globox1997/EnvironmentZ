package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.environmentz.init.ItemInit;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void canWalkOnPowderSnowMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof LivingEntity && ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET).isOf(ItemInit.WOLF_BOOTS))
            info.setReturnValue(true);
    }
}
