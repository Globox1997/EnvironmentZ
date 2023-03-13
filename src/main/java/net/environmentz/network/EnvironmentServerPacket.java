package net.environmentz.network;

import io.netty.buffer.Unpooled;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnvironmentServerPacket {

    public static final Identifier SYNC_ENV_AFFECTED = new Identifier("environmentz", "sync_env_affected");
    public static final Identifier TEMPERATURE_UPDATE = new Identifier("environmentz", "temperature_update");
    public static final Identifier SYNC_VALUES = new Identifier("environmentz", "sync_values");
    public static final Identifier THERMOMETER_UPDATE = new Identifier("environmentz", "thermometer_update");

    public static void init() {
    }

    public static void writeS2CSyncEnvPacket(ServerPlayerEntity serverPlayerEntity, boolean heatAffected, boolean coldAffected) {
        ServerPlayNetworking.send(serverPlayerEntity, SYNC_ENV_AFFECTED, new PacketByteBuf(Unpooled.buffer().writeBoolean(heatAffected).writeBoolean(coldAffected)));
    }

    public static void writeS2CTemperaturePacket(ServerPlayerEntity serverPlayerEntity, int temperature, int wetness) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(temperature);
        buf.writeInt(wetness);
        ServerPlayNetworking.send(serverPlayerEntity, TEMPERATURE_UPDATE, buf);
    }

    public static void writeS2CThermometerPacket(ServerPlayerEntity serverPlayerEntity, int temperature) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(temperature);
        ServerPlayNetworking.send(serverPlayerEntity, THERMOMETER_UPDATE, buf);
    }

    public static void writeS2CSyncValuesPacket(ServerPlayerEntity serverPlayerEntity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        // body temperatures
        buf.writeInt(Temperatures.getBodyTemperatures(0));
        buf.writeInt(Temperatures.getBodyTemperatures(1));
        buf.writeInt(Temperatures.getBodyTemperatures(2));
        buf.writeInt(Temperatures.getBodyTemperatures(3));
        buf.writeInt(Temperatures.getBodyTemperatures(4));
        buf.writeInt(Temperatures.getBodyTemperatures(5));
        buf.writeInt(Temperatures.getBodyTemperatures(6));
        // water max intensity
        buf.writeInt(Temperatures.getBodyWetness(0));
        buf.writeInt(Temperatures.getBodyWetness(1));
        buf.writeInt(Temperatures.getBodyWetness(2));
        buf.writeInt(Temperatures.getBodyWetness(3));
        buf.writeInt(Temperatures.getBodyWetness(4));
        // thermometer temperatures
        buf.writeInt(Temperatures.getThermometerTemperatures(0));
        buf.writeInt(Temperatures.getThermometerTemperatures(1));
        buf.writeInt(Temperatures.getThermometerTemperatures(2));
        buf.writeInt(Temperatures.getThermometerTemperatures(3));

        ServerPlayNetworking.send(serverPlayerEntity, SYNC_VALUES, buf);
    }
}
