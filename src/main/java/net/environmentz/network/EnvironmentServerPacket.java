package net.environmentz.network;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.packet.AffectionPacket;
import net.environmentz.network.packet.SyncValuesPacket;
import net.environmentz.network.packet.TemperaturePacket;
import net.environmentz.network.packet.ThermometerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class EnvironmentServerPacket {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(AffectionPacket.PACKET_ID, AffectionPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(TemperaturePacket.PACKET_ID, TemperaturePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ThermometerPacket.PACKET_ID, ThermometerPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncValuesPacket.PACKET_ID, SyncValuesPacket.PACKET_CODEC);
    }

    public static void writeS2CSyncEnvPacket(ServerPlayerEntity serverPlayerEntity, boolean heatAffected, boolean coldAffected) {
        ServerPlayNetworking.send(serverPlayerEntity, new AffectionPacket(heatAffected, coldAffected));
    }

    public static void writeS2CSyncValuesPacket(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, new SyncValuesPacket(Temperatures.getBodyTemperature(), Temperatures.getBodyWetness(), Temperatures.getThermometerTemperature(),
                Temperatures.getBiomeTemperature()));
    }

    public static void syncTemperature(ServerPlayerEntity serverPlayerEntity, boolean force) {
        TemperatureManager temperatureManager = ((TemperatureManagerAccess) serverPlayerEntity).getTemperatureManager();
        if (force || !temperatureManager.isTemperatureSynced()) {
            ServerPlayNetworking.send(serverPlayerEntity, new TemperaturePacket(temperatureManager.getPlayerTemperature(), temperatureManager.getPlayerWetIntensityValue()));
            temperatureManager.markTemperatureSynced();
        }
    }

    public static void syncThermometer(ServerPlayerEntity serverPlayerEntity, boolean force) {
        TemperatureManager temperatureManager = ((TemperatureManagerAccess) serverPlayerEntity).getTemperatureManager();
        if (force || !temperatureManager.isThermometerSynced()) {
            ServerPlayNetworking.send(serverPlayerEntity, new ThermometerPacket(temperatureManager.getThermometerTemperature()));
            temperatureManager.markThermometerSynced();
        }
    }

    public static void syncAll(ServerPlayerEntity serverPlayerEntity) {
        TemperatureManager temperatureManager = ((TemperatureManagerAccess) serverPlayerEntity).getTemperatureManager();
        writeS2CSyncEnvPacket(serverPlayerEntity, temperatureManager.isHotEnvAffected(), temperatureManager.isColdEnvAffected());
        syncTemperature(serverPlayerEntity, true);
        syncThermometer(serverPlayerEntity, true);
    }
}
