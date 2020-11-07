package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.damage.DamageSource;

@Mixin(DamageSource.class)
public abstract interface DamageSourceAccessor {

    @Invoker("setBypassesArmor")
    public abstract DamageSource setBypassesArmorEnvironmentZAccess();

    @Invoker("setUnblockable")
    public abstract DamageSource setUnblockableEnvironmentZAccess();
}