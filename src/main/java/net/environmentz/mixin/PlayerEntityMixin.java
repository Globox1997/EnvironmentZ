package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.TemperatureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements TemperatureManagerAccess {

    @Unique
    private final TemperatureManager temperatureManager = new TemperatureManager();

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void readCustomDataFromTagMixin(CompoundTag tag, CallbackInfo info) {
        this.temperatureManager.readNbt(tag);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void writeCustomDataToTagMixin(CompoundTag tag, CallbackInfo info) {
        this.temperatureManager.writeNbt(tag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickMixin(CallbackInfo info) {
        this.temperatureManager.tick((Player) (Object) this);
    }

    @Override
    public TemperatureManager getTemperatureManager() {
        return this.temperatureManager;
    }

}