package net.environmentz.network.packet;

import net.environmentz.EnvironmentzMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncValuesPacket(int max_very_cold, int max_cold, int min_cold, int normal, int min_hot, int max_hot, int max_very_hot, int wetness_max, int wetness_soaked, int wetness_water,
                               int wetness_rain, int wetness_dry, int very_cold, int cold, int hot, int very_hot) implements CustomPayload {

    public static final CustomPayload.Id<SyncValuesPacket> PACKET_ID = new CustomPayload.Id<>(EnvironmentzMain.identifierOf("sync_values_packet"));

    public static final PacketCodec<RegistryByteBuf, SyncValuesPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.max_very_cold());
        buf.writeInt(value.max_cold());
        buf.writeInt(value.min_cold());
        buf.writeInt(value.normal());
        buf.writeInt(value.min_hot());
        buf.writeInt(value.max_hot());
        buf.writeInt(value.max_very_hot());
        // water max intensity
        buf.writeInt(value.wetness_max());
        buf.writeInt(value.wetness_soaked());
        buf.writeInt(value.wetness_water());
        buf.writeInt(value.wetness_rain());
        buf.writeInt(value.wetness_dry());
        // thermometer temperatures
        buf.writeInt(value.very_cold());
        buf.writeInt(value.cold());
        buf.writeInt(value.hot());
        buf.writeInt(value.very_hot());

    }, buf -> new SyncValuesPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

