package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ThermometerTemperature(int veryCold, int cold, int hot, int veryHot) {

    public static final ThermometerTemperature EMPTY = new ThermometerTemperature(0, 0, 0, 0);

    public static final Codec<ThermometerTemperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("very_cold").forGetter(ThermometerTemperature::veryCold),
            Codec.INT.fieldOf("cold").forGetter(ThermometerTemperature::cold),
            Codec.INT.fieldOf("hot").forGetter(ThermometerTemperature::hot),
            Codec.INT.fieldOf("very_hot").forGetter(ThermometerTemperature::veryHot)
    ).apply(instance, ThermometerTemperature::new));
}
