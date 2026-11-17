package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TemperaturePacket(int temperature, int wetness) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TemperaturePacket> PACKET_ID = new CustomPacketPayload.Type<>(EnvironmentzMain.identifierOf("temperature_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TemperaturePacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.temperature());
        buf.writeInt(value.wetness());
    }, buf -> new TemperaturePacket(buf.readInt(), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

