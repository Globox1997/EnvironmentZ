package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EffectTemperature(int temperature, int heatProtection, int coldProtection) {

    public static final Codec<EffectTemperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("temperature").forGetter(EffectTemperature::temperature),
            Codec.INT.optionalFieldOf("heat_protection", 0).forGetter(EffectTemperature::heatProtection),
            Codec.INT.optionalFieldOf("cold_protection", 0).forGetter(EffectTemperature::coldProtection)
    ).apply(instance, EffectTemperature::new));
}
