package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BodyWetness(int max, int soaked, int water, int rain, int dry) {

    public static final BodyWetness EMPTY = new BodyWetness(0, 0, 0, 0, 0);

    public static final Codec<BodyWetness> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("max_wetness").forGetter(BodyWetness::max),
            Codec.INT.fieldOf("soaked").forGetter(BodyWetness::soaked),
            Codec.INT.fieldOf("water").forGetter(BodyWetness::water),
            Codec.INT.fieldOf("rain").forGetter(BodyWetness::rain),
            Codec.INT.fieldOf("dry").forGetter(BodyWetness::dry)
    ).apply(instance, BodyWetness::new));
}
