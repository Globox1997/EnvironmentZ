package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BodyTemperature(int maxVeryCold, int maxCold, int minCold, int normal, int minHot, int maxHot, int maxVeryHot) {

    public static final BodyTemperature EMPTY = new BodyTemperature(0, 0, 0, 0, 0, 0, 0);

    public static final Codec<BodyTemperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("max_very_cold").forGetter(BodyTemperature::maxVeryCold),
            Codec.INT.fieldOf("max_cold").forGetter(BodyTemperature::maxCold),
            Codec.INT.fieldOf("min_cold").forGetter(BodyTemperature::minCold),
            Codec.INT.fieldOf("normal").forGetter(BodyTemperature::normal),
            Codec.INT.fieldOf("min_hot").forGetter(BodyTemperature::minHot),
            Codec.INT.fieldOf("max_hot").forGetter(BodyTemperature::maxHot),
            Codec.INT.fieldOf("max_very_hot").forGetter(BodyTemperature::maxVeryHot)
    ).apply(instance, BodyTemperature::new));
}
