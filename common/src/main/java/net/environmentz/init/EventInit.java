package net.environmentz.init;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class EventInit {

    public static void onPlayerChangeLevel(ServerPlayer player) {
        ((PlayerEnvAccess) player).compatSync();
    }

    public static void onPlayerJoin(ServerPlayer player) {
        EnvironmentServerPacket.writeS2CSyncValuesPacket(player);
        EnvironmentServerPacket.syncAll(player);
    }

    public static void onPlayerRespawn(ServerPlayer newPlayer) {
        EnvironmentServerPacket.syncAll(newPlayer);
    }

    public static void onDataPackReload(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            EnvironmentServerPacket.writeS2CSyncValuesPacket(player);
        }
    }
}
