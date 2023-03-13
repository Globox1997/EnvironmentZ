package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.TemperatureManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements TemperatureManagerAccess {

    private final TemperatureManager temperatureManager = new TemperatureManager();

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readCustomDataFromTagMixin(NbtCompound tag, CallbackInfo info) {
        this.temperatureManager.readNbt(tag);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeCustomDataToTagMixin(NbtCompound tag, CallbackInfo info) {
        this.temperatureManager.writeNbt(tag);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickMixin(CallbackInfo info) {
        this.temperatureManager.tick((PlayerEntity) (Object) this);
    }

    @Override
    public TemperatureManager getTemperatureManager() {
        return this.temperatureManager;
    }

}