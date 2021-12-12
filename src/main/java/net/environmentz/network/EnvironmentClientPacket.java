package net.environmentz.network;

import net.environmentz.access.PlayerEnvAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class EnvironmentClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(EnvironmentServerPacket.SYNC_ENV_AFFECTED, (client, handler, buffer, responseSender) -> {
            boolean isHotAffected = buffer.readBoolean();
            boolean isColdAffected = buffer.readBoolean();
            client.execute(() -> {
                ((PlayerEnvAccess) client.player).setHotEnvAffected(isHotAffected);
                ((PlayerEnvAccess) client.player).setColdEnvAffected(isColdAffected);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(EnvironmentServerPacket.TEMPERATURE_UPDATE, (client, handler, buffer, responseSender) -> {
            int temperature = buffer.readInt();
            int wetness = buffer.readInt();
            client.execute(() -> {
                ((PlayerEnvAccess) client.player).setPlayerTemperature(temperature);
                ((PlayerEnvAccess) client.player).setPlayerWetIntensityValue(wetness);
            });
        });
    }
}
