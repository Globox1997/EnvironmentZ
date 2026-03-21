package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record TemperaturePacket(int temperature, int wetness) implements CustomPayload {

    public static final CustomPayload.Id<TemperaturePacket> PACKET_ID = new CustomPayload.Id<>(EnvironmentzMain.identifierOf("temperature_packet"));

    public static final PacketCodec<RegistryByteBuf, TemperaturePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.temperature());
        buf.writeInt(value.wetness());
    }, buf -> new TemperaturePacket(buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

