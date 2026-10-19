package net.environmentz.network;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.packet.AffectionPacket;
import net.environmentz.network.packet.SyncValuesPacket;
import net.environmentz.network.packet.TemperaturePacket;
import net.environmentz.network.packet.ThermometerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class EnvironmentClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(AffectionPacket.PACKET_ID, (payload, context) -> {
            boolean isHotAffected = payload.heatAffected();
            boolean isColdAffected = payload.coldAffected();
            context.client().execute(() -> {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess) context.player()).getTemperatureManager();
                temperatureManager.setEnvironmentAffection(isHotAffected, isColdAffected);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(TemperaturePacket.PACKET_ID, (payload, context) -> {
            int temperature = payload.temperature();
            int wetness = payload.wetness();
            context.client().execute(() -> {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess)  context.player()).getTemperatureManager();

                temperatureManager.setPlayerTemperature(temperature);
                temperatureManager.setPlayerWetIntensityValue(wetness);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ThermometerPacket.PACKET_ID, (payload, context) -> {
            int temperature = payload.temperature();
            context.client().execute(() -> {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess)  context.player()).getTemperatureManager();
                temperatureManager.setThermometerTemperature(temperature);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(SyncValuesPacket.PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                Temperatures.setBodyTemperature(payload.bodyTemperature());
                Temperatures.setBodyWetness(payload.bodyWetness());
                Temperatures.setThermometerTemperature(payload.thermometerTemperature());
                Temperatures.setBiomeTemperature(payload.biomeTemperature());
            });
        });
    }
}
