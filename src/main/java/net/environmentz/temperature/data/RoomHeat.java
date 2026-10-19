package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RoomHeat(float factor, int enclosedRadius) {

    public static final RoomHeat DEFAULT = new RoomHeat(0.5F, 3);

    public static final Codec<RoomHeat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("factor").forGetter(RoomHeat::factor),
            Codec.INT.fieldOf("enclosed_radius").forGetter(RoomHeat::enclosedRadius)
    ).apply(instance, RoomHeat::new));
}
