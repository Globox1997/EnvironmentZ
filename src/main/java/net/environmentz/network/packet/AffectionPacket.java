package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record AffectionPacket(boolean heatAffected, boolean coldAffected) implements CustomPayload {

    public static final CustomPayload.Id<AffectionPacket> PACKET_ID = new CustomPayload.Id<>(EnvironmentzMain.identifierOf("affection_packet"));

    public static final PacketCodec<RegistryByteBuf, AffectionPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBoolean(value.heatAffected());
        buf.writeBoolean(value.coldAffected());
    }, buf -> new AffectionPacket(buf.readBoolean(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

