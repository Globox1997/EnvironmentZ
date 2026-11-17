package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AffectionPacket(boolean heatAffected, boolean coldAffected) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AffectionPacket> PACKET_ID = new CustomPacketPayload.Type<>(EnvironmentzMain.identifierOf("affection_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AffectionPacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeBoolean(value.heatAffected());
        buf.writeBoolean(value.coldAffected());
    }, buf -> new AffectionPacket(buf.readBoolean(), buf.readBoolean()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

