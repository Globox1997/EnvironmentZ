package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.environmentz.temperature.data.BiomeTemperature;
import net.environmentz.temperature.data.BodyTemperature;
import net.environmentz.temperature.data.BodyWetness;
import net.environmentz.temperature.data.ThermometerTemperature;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncValuesPacket(BodyTemperature bodyTemperature, BodyWetness bodyWetness, ThermometerTemperature thermometerTemperature,
                               BiomeTemperature biomeTemperature) implements CustomPayload {

    public static final CustomPayload.Id<SyncValuesPacket> PACKET_ID = new CustomPayload.Id<>(EnvironmentzMain.identifierOf("sync_values_packet"));

    public static final PacketCodec<RegistryByteBuf, SyncValuesPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(BodyTemperature.CODEC), SyncValuesPacket::bodyTemperature,
            PacketCodecs.codec(BodyWetness.CODEC), SyncValuesPacket::bodyWetness,
            PacketCodecs.codec(ThermometerTemperature.CODEC), SyncValuesPacket::thermometerTemperature,
            PacketCodecs.codec(BiomeTemperature.CODEC), SyncValuesPacket::biomeTemperature,
            SyncValuesPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}
