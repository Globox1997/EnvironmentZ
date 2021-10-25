package net.environmentz.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnvironmentServerPacket {

    public static final Identifier SYNC_ENV_AFFECTED = new Identifier("environmentz", "sync_env_affected");

    public static void init() {
    }

    public static void writeS2CSyncEnvPacket(ServerPlayerEntity serverPlayerEntity, boolean heatAffected, boolean coldAffected) {
        ServerPlayNetworking.send(serverPlayerEntity, SYNC_ENV_AFFECTED, new PacketByteBuf(Unpooled.buffer().writeBoolean(heatAffected).writeBoolean(coldAffected)));
    }
}
