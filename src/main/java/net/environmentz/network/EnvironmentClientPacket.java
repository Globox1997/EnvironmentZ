package net.environmentz.network;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class EnvironmentClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(EnvironmentServerPacket.SYNC_ENV_AFFECTED, (client, handler, buffer, responseSender) -> {
            boolean isHotAffected = buffer.readBoolean();
            boolean isColdAffected = buffer.readBoolean();
            client.execute(() -> {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess) client.player).getTemperatureManager();

                temperatureManager.setEnvironmentAffection(isHotAffected, isColdAffected);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(EnvironmentServerPacket.TEMPERATURE_UPDATE, (client, handler, buffer, responseSender) -> {
            int temperature = buffer.readInt();
            int wetness = buffer.readInt();
            client.execute(() -> {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess) client.player).getTemperatureManager();

                temperatureManager.setPlayerTemperature(temperature);
                temperatureManager.setPlayerWetIntensityValue(wetness);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(EnvironmentServerPacket.THERMOMETER_UPDATE, (client, handler, buffer, responseSender) -> {
            int temperature = buffer.readInt();
            client.execute(() -> {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess) client.player).getTemperatureManager();
                temperatureManager.setThermometerTemperature(temperature);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(EnvironmentServerPacket.SYNC_VALUES, (client, handler, buffer, responseSender) -> {
            int max_very_cold = buffer.readInt();
            int max_cold = buffer.readInt();
            int min_cold = buffer.readInt();
            int normal = buffer.readInt();
            int min_hot = buffer.readInt();
            int max_hot = buffer.readInt();
            int max_very_hot = buffer.readInt();

            int wetness_max = buffer.readInt();
            int wetness_soaked = buffer.readInt();
            int wetness_water = buffer.readInt();
            int wetness_rain = buffer.readInt();
            int wetness_dry = buffer.readInt();

            int very_cold = buffer.readInt();
            int cold = buffer.readInt();
            int hot = buffer.readInt();
            int very_hot = buffer.readInt();
            client.execute(() -> {
                Temperatures.setBodyTemperatures(max_very_cold, max_cold, min_cold, normal, min_hot, max_hot, max_very_hot);
                Temperatures.setBodyWetness(wetness_max, wetness_soaked, wetness_water, wetness_rain, wetness_dry);
                Temperatures.setThermometerTemperatures(very_cold, cold, hot, very_hot);
            });
        });
    }
}
