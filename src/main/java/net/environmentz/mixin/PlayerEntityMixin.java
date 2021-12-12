package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.util.TemperatureAspects;
import net.environmentz.access.PlayerEnvAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEnvAccess {
    private int environmentTicker;
    private boolean isHotEnvAffected = true;
    private boolean isColdEnvAffected = true;

    private boolean playerHasHeatProtection = false;
    private boolean playerHasColdProtection = false;
    private int playerTemperature = 0;
    private int playerColdProtectionAmount = 0;
    private int playerHotProtectionAmount = 0;
    private int playerWetIntensityValue = 0;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readCustomDataFromTagMixin(NbtCompound tag, CallbackInfo info) {
        this.isHotEnvAffected = tag.getBoolean("IsHotEnvAffected");
        this.isColdEnvAffected = tag.getBoolean("IsColdEnvAffected");
        this.playerTemperature = tag.getInt("PlayerTemperature");
        this.playerColdProtectionAmount = tag.getInt("PlayerColdProtectionAmount");
        this.playerHotProtectionAmount = tag.getInt("PlayerHotProtectionAmount");
        this.playerWetIntensityValue = tag.getInt("PlayerWetIntensityValue");
        this.playerHasHeatProtection = tag.getBoolean("PlayerHasHeatProtection");
        this.playerHasColdProtection = tag.getBoolean("PlayerHasColdProtection");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeCustomDataToTagMixin(NbtCompound tag, CallbackInfo info) {
        tag.putBoolean("IsHotEnvAffected", this.isHotEnvAffected);
        tag.putBoolean("IsColdEnvAffected", this.isColdEnvAffected);
        tag.putInt("PlayerTemperature", this.playerTemperature);
        tag.putInt("PlayerColdProtectionAmount", this.playerColdProtectionAmount);
        tag.putInt("PlayerHotProtectionAmount", this.playerHotProtectionAmount);
        tag.putInt("PlayerWetIntensityValue", this.playerWetIntensityValue);
        tag.putBoolean("PlayerHasHeatProtection", this.playerHasHeatProtection);
        tag.putBoolean("PlayerHasColdProtection", this.playerHasColdProtection);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickMixin(CallbackInfo info) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (!this.world.isClient && !playerEntity.isCreative() && !playerEntity.isSpectator() && playerEntity.isAlive()) {
            if (environmentTicker++ >= 20) {
                TemperatureAspects.tickPlayerEnvironment(playerEntity, environmentTicker);
                // System.out.println(UUID.randomUUID());
                // System.out.println("Environment: " + TemperatureAspects.dehydrationTimer);

                // if (this.world.getBiome(this.getBlockPos()).getTemperature() <= ConfigInit.CONFIG.biome_freeze_temp) {
                // TemperatureAspects.coldEnvironment(playerEntity);
                // } else if (TemperatureAspects.coldnessTimer > 0) {
                // TemperatureAspects.coldnessTimer -= 2;
                // } else if (this.world.getBiome(this.getBlockPos()).getTemperature() >= ConfigInit.CONFIG.biome_overheat_temp) {
                // TemperatureAspects.hotEnvironment(playerEntity);
                // } else if (TemperatureAspects.dehydrationTimer > 0) {
                // TemperatureAspects.dehydrationTimer -= 2;
                // }
                // if (this.hasStatusEffect(EffectInit.COLDNESS) || this.hasStatusEffect(EffectInit.OVERHEATING)) {
                // TemperatureAspects.acclimatize(playerEntity);
                // } else if (TemperatureAspects.acclimatizeTimer > 0) {
                // TemperatureAspects.acclimatizeTimer -= 2;
                // }
                // TemperatureAspects.dryOrWett(playerEntity);
                environmentTicker = 0;
            }
        }
    }

    @Override
    public void setHotEnvAffected(boolean affected) {
        this.isHotEnvAffected = affected;
    }

    @Override
    public void setColdEnvAffected(boolean affected) {
        this.isColdEnvAffected = affected;
    }

    @Override
    public boolean isHotEnvAffected() {
        return this.isHotEnvAffected;
    }

    @Override
    public boolean isColdEnvAffected() {
        return this.isColdEnvAffected;
    }

    @Override
    public void setPlayerTemperature(int temperature) {
        this.playerTemperature = temperature;
    }

    @Override
    public void setPlayerHeatProtectionAmount(int amount) {
        this.playerHotProtectionAmount = amount;
    }

    @Override
    public void setPlayerColdProtectionAmount(int amount) {
        this.playerColdProtectionAmount = amount;
    }

    @Override
    public void setPlayerWetIntensityValue(int amount) {
        this.playerWetIntensityValue = amount;
    }

    @Override
    public int getPlayerTemperature() {
        return this.playerTemperature;
    }

    @Override
    public int getPlayerWetIntensityValue() {
        return this.playerWetIntensityValue;
    }

    @Override
    public int getPlayerHeatProtectionAmount() {
        return this.playerHotProtectionAmount;
    }

    @Override
    public int getPlayerColdProtectionAmount() {
        return this.playerColdProtectionAmount;
    }
}