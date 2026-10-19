package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BodyProtection(int maxHeat, int maxCold, int maxHeatResistance, int maxColdResistance) {

    public static final BodyProtection EMPTY = new BodyProtection(0, 0, 0, 0);

    public static final Codec<BodyProtection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("max_heat").forGetter(BodyProtection::maxHeat),
            Codec.INT.fieldOf("max_cold").forGetter(BodyProtection::maxCold),
            Codec.INT.fieldOf("max_heat_resistance").forGetter(BodyProtection::maxHeatResistance),
            Codec.INT.fieldOf("max_cold_resistance").forGetter(BodyProtection::maxColdResistance)
    ).apply(instance, BodyProtection::new));
}
