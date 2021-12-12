package net.environmentz.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Invoker("isBeingRainedOn")
    boolean callIsBeingRainedOn();
}
