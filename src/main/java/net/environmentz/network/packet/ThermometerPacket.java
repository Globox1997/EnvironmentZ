package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ThermometerPacket(int temperature) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ThermometerPacket> PACKET_ID = new CustomPacketPayload.Type<>(EnvironmentzMain.identifierOf("thermometer_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ThermometerPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.temperature());
    }, buf -> new ThermometerPacket(buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

