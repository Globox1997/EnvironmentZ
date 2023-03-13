package net.environmentz.temperature;

import net.environmentz.init.ConfigInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class TemperatureManager {

    private int environmentTicker;
    private int environmentTickerCount;
    private boolean isHotEnvAffected = true;
    private boolean isColdEnvAffected = true;

    private boolean playerHasHeatProtection = false;
    private boolean playerHasColdProtection = false;
    private int playerTemperature = 0;
    private int playerColdProtectionAmount = 0;
    private int playerHotProtectionAmount = 0;
    private int playerWetIntensityValue = 0;
    private int playerHeatResistance = 0;
    private int playerColdResistance = 0;

    private int thermometerCalming = 0;
    private int thermometerTemperature = 0;

    public void readNbt(NbtCompound nbt) {
        this.isHotEnvAffected = nbt.getBoolean("IsHotEnvAffected");
        this.isColdEnvAffected = nbt.getBoolean("IsColdEnvAffected");
        this.playerTemperature = nbt.getInt("PlayerTemperature");
        this.playerColdProtectionAmount = nbt.getInt("PlayerColdProtectionAmount");
        this.playerHotProtectionAmount = nbt.getInt("PlayerHotProtectionAmount");
        this.playerWetIntensityValue = nbt.getInt("PlayerWetIntensityValue");
        this.playerHasHeatProtection = nbt.getBoolean("PlayerHasHeatProtection");
        this.playerHasColdProtection = nbt.getBoolean("PlayerHasColdProtection");
        this.thermometerTemperature = nbt.getInt("ThermometerTemperature");
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("IsHotEnvAffected", this.isHotEnvAffected);
        nbt.putBoolean("IsColdEnvAffected", this.isColdEnvAffected);
        nbt.putInt("PlayerTemperature", this.playerTemperature);
        nbt.putInt("PlayerColdProtectionAmount", this.playerColdProtectionAmount);
        nbt.putInt("PlayerHotProtectionAmount", this.playerHotProtectionAmount);
        nbt.putInt("PlayerWetIntensityValue", this.playerWetIntensityValue);
        nbt.putBoolean("PlayerHasHeatProtection", this.playerHasHeatProtection);
        nbt.putBoolean("PlayerHasColdProtection", this.playerHasColdProtection);
        nbt.putInt("ThermometerTemperature", this.thermometerTemperature);
    }

    public void tick(PlayerEntity playerEntity) {
        if (!playerEntity.world.isClient && !playerEntity.isCreative() && !playerEntity.isSpectator() && playerEntity.isAlive()) {
            if (environmentTicker++ >= ConfigInit.CONFIG.temperature_calculation_time) {
                TemperatureAspects.tickPlayerEnvironment(this, playerEntity, environmentTickerCount);
                environmentTicker = 0;
                environmentTickerCount++;
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

    // Setter
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

    // Getter
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

    @Environment(EnvType.CLIENT)
    public void setThermometerCalm(int ticks) {
        this.thermometerCalming = ticks;
    }

    @Environment(EnvType.CLIENT)
    public int getThermometerCalm() {
        return this.thermometerCalming;
    }
}
