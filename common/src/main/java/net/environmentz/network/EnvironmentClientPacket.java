package net.environmentz.network;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.packet.AffectionPacket;
import net.environmentz.network.packet.SyncValuesPacket;
import net.environmentz.network.packet.TemperaturePacket;
import net.environmentz.network.packet.ThermometerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class EnvironmentClientPacket {

    public static void handleAffection(AffectionPacket payload) {
        TemperatureManager temperatureManager = getTemperatureManager();
        if (temperatureManager != null) {
            temperatureManager.setEnvironmentAffection(payload.heatAffected(), payload.coldAffected());
        }
    }

    public static void handleTemperature(TemperaturePacket payload) {
        TemperatureManager temperatureManager = getTemperatureManager();
        if (temperatureManager != null) {
            temperatureManager.setPlayerTemperature(payload.temperature());
            temperatureManager.setPlayerWetIntensityValue(payload.wetness());
        }
    }

    public static void handleThermometer(ThermometerPacket payload) {
        TemperatureManager temperatureManager = getTemperatureManager();
        if (temperatureManager != null) {
            temperatureManager.setThermometerTemperature(payload.temperature());
        }
    }

    public static void handleSyncValues(SyncValuesPacket payload) {
        Temperatures.setBodyTemperature(payload.bodyTemperature());
        Temperatures.setBodyWetness(payload.bodyWetness());
        Temperatures.setThermometerTemperature(payload.thermometerTemperature());
        Temperatures.setBiomeTemperature(payload.biomeTemperature());
    }

    private static TemperatureManager getTemperatureManager() {
        Player player = Minecraft.getInstance().player;
        return player != null ? ((TemperatureManagerAccess) player).getTemperatureManager() : null;
    }
}
