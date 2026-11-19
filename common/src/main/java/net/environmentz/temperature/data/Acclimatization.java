package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Acclimatization(int hotBodyTemperature, int hotBody, int veryHotBodyTemperature, int veryHotBody, int coldBodyTemperature, int coldBody, int veryColdBodyTemperature,
                              int veryColdBody) {

    public static final Acclimatization EMPTY = new Acclimatization(0, 0, 0, 0, 0, 0, 0, 0);

    public static final Codec<Acclimatization> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hot_body_temperature").forGetter(Acclimatization::hotBodyTemperature),
            Codec.INT.fieldOf("hot_body").forGetter(Acclimatization::hotBody),
            Codec.INT.fieldOf("very_hot_body_temperature").forGetter(Acclimatization::veryHotBodyTemperature),
            Codec.INT.fieldOf("very_hot_body").forGetter(Acclimatization::veryHotBody),
            Codec.INT.fieldOf("cold_body_temperature").forGetter(Acclimatization::coldBodyTemperature),
            Codec.INT.fieldOf("cold_body").forGetter(Acclimatization::coldBody),
            Codec.INT.fieldOf("very_cold_body_temperature").forGetter(Acclimatization::veryColdBodyTemperature),
            Codec.INT.fieldOf("very_cold_body").forGetter(Acclimatization::veryColdBody)
    ).apply(instance, Acclimatization::new));
}
