package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.environmentz.temperature.data.BiomeTemperature;
import net.environmentz.temperature.data.BodyTemperature;
import net.environmentz.temperature.data.BodyWetness;
import net.environmentz.temperature.data.ThermometerTemperature;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncValuesPacket(BodyTemperature bodyTemperature, BodyWetness bodyWetness, ThermometerTemperature thermometerTemperature,
                               BiomeTemperature biomeTemperature) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncValuesPacket> PACKET_ID = new CustomPacketPayload.Type<>(EnvironmentzMain.identifierOf("sync_values_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncValuesPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(BodyTemperature.CODEC), SyncValuesPacket::bodyTemperature,
            ByteBufCodecs.fromCodec(BodyWetness.CODEC), SyncValuesPacket::bodyWetness,
            ByteBufCodecs.fromCodec(ThermometerTemperature.CODEC), SyncValuesPacket::thermometerTemperature,
            ByteBufCodecs.fromCodec(BiomeTemperature.CODEC), SyncValuesPacket::biomeTemperature,
            SyncValuesPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}
