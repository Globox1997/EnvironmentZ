package net.environmentz.temperature.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record ArmorTiers(float veryCold, float cold, float normal, float hot, float veryHot) {

    public static final ArmorTiers ZERO = uniform(0.0F);

    private static final Codec<ArmorTiers> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("very_cold").forGetter(ArmorTiers::veryCold),
            Codec.FLOAT.fieldOf("cold").forGetter(ArmorTiers::cold),
            Codec.FLOAT.fieldOf("normal").forGetter(ArmorTiers::normal),
            Codec.FLOAT.fieldOf("hot").forGetter(ArmorTiers::hot),
            Codec.FLOAT.fieldOf("very_hot").forGetter(ArmorTiers::veryHot)
    ).apply(instance, ArmorTiers::new));

    public static final Codec<ArmorTiers> CODEC = Codec.either(Codec.FLOAT, OBJECT_CODEC).xmap(either -> either.map(ArmorTiers::uniform, Function.identity()), Either::right);

    public static ArmorTiers uniform(float value) {
        return new ArmorTiers(value, value, value, value, value);
    }

    public float get(EnvironmentCode environmentCode) {
        return switch (environmentCode) {
            case VERY_COLD -> this.veryCold;
            case COLD -> this.cold;
            case NORMAL -> this.normal;
            case HOT -> this.hot;
            case VERY_HOT -> this.veryHot;
        };
    }
}
