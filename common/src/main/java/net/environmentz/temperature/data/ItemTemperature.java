package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemTemperature(int temperature, int damage, int heatProtection, int coldProtection) {

    public static final Codec<ItemTemperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("temperature").forGetter(ItemTemperature::temperature),
            Codec.INT.optionalFieldOf("damage", 0).forGetter(ItemTemperature::damage),
            Codec.INT.optionalFieldOf("heat_protection", 0).forGetter(ItemTemperature::heatProtection),
            Codec.INT.optionalFieldOf("cold_protection", 0).forGetter(ItemTemperature::coldProtection)
    ).apply(instance, ItemTemperature::new));
}
