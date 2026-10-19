package net.environmentz.temperature;

import net.environmentz.init.ConfigInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class TemperatureManager {

    private static final int NOT_SYNCED = Integer.MIN_VALUE;

    private int environmentTicker;
    private boolean isHotEnvAffected = true;
    private boolean isColdEnvAffected = true;

    private int playerTemperature = 0;
    private int playerColdProtectionAmount = 0;
    private int playerHotProtectionAmount = 0;
    private int playerWetIntensityValue = 0;
    private int playerHeatResistance = 0;
    private int playerColdResistance = 0;

    private int thermometerCalming = 0;
    private int thermometerTemperature = 0;

    private int syncedTemperature = NOT_SYNCED;
    private int syncedWetness = NOT_SYNCED;
    private int syncedThermometerTemperature = NOT_SYNCED;

    public void readNbt(NbtCompound nbt) {
        this.isHotEnvAffected = !nbt.contains("IsHotEnvAffected") || nbt.getBoolean("IsHotEnvAffected");
        this.isColdEnvAffected = !nbt.contains("IsColdEnvAffected") || nbt.getBoolean("IsColdEnvAffected");
        this.playerTemperature = nbt.getInt("PlayerTemperature");
        this.playerColdProtectionAmount = nbt.getInt("PlayerColdProtectionAmount");
        this.playerHotProtectionAmount = nbt.getInt("PlayerHotProtectionAmount");
        this.playerWetIntensityValue = nbt.getInt("PlayerWetIntensityValue");
        this.playerHeatResistance = nbt.getInt("PlayerHeatResistance");
        this.playerColdResistance = nbt.getInt("PlayerColdResistance");
        this.thermometerTemperature = nbt.getInt("ThermometerTemperature");
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("IsHotEnvAffected", this.isHotEnvAffected);
        nbt.putBoolean("IsColdEnvAffected", this.isColdEnvAffected);
        nbt.putInt("PlayerTemperature", this.playerTemperature);
        nbt.putInt("PlayerColdProtectionAmount", this.playerColdProtectionAmount);
        nbt.putInt("PlayerHotProtectionAmount", this.playerHotProtectionAmount);
        nbt.putInt("PlayerWetIntensityValue", this.playerWetIntensityValue);
        nbt.putInt("PlayerHeatResistance", this.playerHeatResistance);
        nbt.putInt("PlayerColdResistance", this.playerColdResistance);
        nbt.putInt("ThermometerTemperature", this.thermometerTemperature);
    }

    public void copyFrom(TemperatureManager oldManager, boolean alive) {
        this.isHotEnvAffected = oldManager.isHotEnvAffected;
        this.isColdEnvAffected = oldManager.isColdEnvAffected;
        this.playerHeatResistance = oldManager.playerHeatResistance;
        this.playerColdResistance = oldManager.playerColdResistance;
        if (alive) {
            this.playerTemperature = oldManager.playerTemperature;
            this.playerColdProtectionAmount = oldManager.playerColdProtectionAmount;
            this.playerHotProtectionAmount = oldManager.playerHotProtectionAmount;
            this.playerWetIntensityValue = oldManager.playerWetIntensityValue;
            this.thermometerTemperature = oldManager.thermometerTemperature;
        }
    }

    public void tick(PlayerEntity playerEntity) {
        if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity && !playerEntity.isCreative() && !playerEntity.isSpectator() && playerEntity.isAlive()) {
            if (this.environmentTicker++ >= ConfigInit.CONFIG.temperatureCalculationTime) {
                TemperatureAspects.tickPlayerEnvironment(this, serverPlayerEntity);
                this.environmentTicker = 0;
            }
        }
    }

    public void setEnvironmentAffection(boolean heatAffected, boolean coldAffected) {
        this.isHotEnvAffected = heatAffected;
        this.isColdEnvAffected = coldAffected;
    }

    public boolean isHotEnvAffected() {
        return this.isHotEnvAffected;
    }

    public boolean isColdEnvAffected() {
        return this.isColdEnvAffected;
    }

    public void setPlayerTemperature(int temperature) {
        this.playerTemperature = temperature;
    }

    public void setPlayerHeatProtectionAmount(int amount) {
        this.playerHotProtectionAmount = amount;
    }

    public void setPlayerColdProtectionAmount(int amount) {
        this.playerColdProtectionAmount = amount;
    }

    public void setPlayerWetIntensityValue(int amount) {
        this.playerWetIntensityValue = amount;
    }

    public void setPlayerHeatResistance(int amount) {
        this.playerHeatResistance = amount;
    }

    public void setPlayerColdResistance(int amount) {
        this.playerColdResistance = amount;
    }

    public void setThermometerTemperature(int temperature) {
        this.thermometerTemperature = temperature;
    }

    public int getPlayerTemperature() {
        return this.playerTemperature;
    }

    public int getPlayerWetIntensityValue() {
        return this.playerWetIntensityValue;
    }

    public int getPlayerHeatProtectionAmount() {
        return this.playerHotProtectionAmount;
    }

    public int getPlayerColdProtectionAmount() {
        return this.playerColdProtectionAmount;
    }

    public int getPlayerHeatResistance() {
        return this.playerHeatResistance;
    }

    public int getPlayerColdResistance() {
        return this.playerColdResistance;
    }

    public int getThermometerTemperature() {
        return this.thermometerTemperature;
    }

    public boolean isTemperatureSynced() {
        return this.syncedTemperature == this.playerTemperature && this.syncedWetness == this.playerWetIntensityValue;
    }

    public void markTemperatureSynced() {
        this.syncedTemperature = this.playerTemperature;
        this.syncedWetness = this.playerWetIntensityValue;
    }

    public boolean isThermometerSynced() {
        return this.syncedThermometerTemperature == this.thermometerTemperature;
    }

    public void markThermometerSynced() {
        this.syncedThermometerTemperature = this.thermometerTemperature;
    }

    @Environment(EnvType.CLIENT)
    public void setThermometerCalm(int ticks) {
        this.thermometerCalming = ticks;
    }

    @Environment(EnvType.CLIENT)
    public int getThermometerCalm() {
        return this.thermometerCalming;
    }
}
