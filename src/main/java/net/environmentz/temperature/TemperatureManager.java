package net.environmentz.temperature;

import net.environmentz.init.ConfigInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    private boolean loadedFromSave = false;

    private int syncedTemperature = NOT_SYNCED;
    private int syncedWetness = NOT_SYNCED;
    private int syncedThermometerTemperature = NOT_SYNCED;

    public void readNbt(ValueInput nbt) {
        this.loadedFromSave = true;
        this.isHotEnvAffected = nbt.getBooleanOr("IsHotEnvAffected", true);
        this.isColdEnvAffected = nbt.getBooleanOr("IsColdEnvAffected", true);
        this.playerTemperature = nbt.getIntOr("PlayerTemperature", 0);
        this.playerColdProtectionAmount = nbt.getIntOr("PlayerColdProtectionAmount", 0);
        this.playerHotProtectionAmount = nbt.getIntOr("PlayerHotProtectionAmount", 0);
        this.playerWetIntensityValue = nbt.getIntOr("PlayerWetIntensityValue", 0);
        this.playerHeatResistance = nbt.getIntOr("PlayerHeatResistance", 0);
        this.playerColdResistance = nbt.getIntOr("PlayerColdResistance", 0);
        this.thermometerTemperature = nbt.getIntOr("ThermometerTemperature", 0);
    }

    public void writeNbt(ValueOutput nbt) {
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

    public void tick(Player playerEntity) {
        if (playerEntity instanceof ServerPlayer serverPlayerEntity && !playerEntity.isCreative() && !playerEntity.isSpectator() && playerEntity.isAlive()) {
            if (++this.environmentTicker >= ConfigInit.CONFIG.temperatureCalculationTime) {
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
        this.playerHotProtectionAmount = Mth.clamp(amount, 0, Temperatures.getBodyProtection().maxHeat());
    }

    public void setPlayerColdProtectionAmount(int amount) {
        this.playerColdProtectionAmount = Mth.clamp(amount, 0, Temperatures.getBodyProtection().maxCold());
    }

    public void setPlayerWetIntensityValue(int amount) {
        this.playerWetIntensityValue = amount;
    }

    public void setPlayerHeatResistance(int amount) {
        this.playerHeatResistance = Mth.clamp(amount, 0, Temperatures.getBodyProtection().maxHeatResistance());
    }

    public void setPlayerColdResistance(int amount) {
        this.playerColdResistance = Mth.clamp(amount, 0, Temperatures.getBodyProtection().maxColdResistance());
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

    public boolean isLoadedFromSave() {
        return this.loadedFromSave;
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
