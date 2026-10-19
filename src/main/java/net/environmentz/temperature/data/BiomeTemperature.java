package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BiomeTemperature(float veryCold, float cold, float hot, float veryHot) {

    public static final BiomeTemperature EMPTY = new BiomeTemperature(0.0F, 0.0F, 0.0F, 0.0F);

    public static final Codec<BiomeTemperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("very_cold").forGetter(BiomeTemperature::veryCold),
            Codec.FLOAT.fieldOf("cold").forGetter(BiomeTemperature::cold),
            Codec.FLOAT.fieldOf("hot").forGetter(BiomeTemperature::hot),
            Codec.FLOAT.fieldOf("very_hot").forGetter(BiomeTemperature::veryHot)
    ).apply(instance, BiomeTemperature::new));
}
