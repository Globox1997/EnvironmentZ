package net.environmentz.temperature.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record TemperatureTiers(int veryCold, int cold, int normal, int hot, int veryHot) {

    public static final TemperatureTiers ZERO = uniform(0);

    private static final Codec<TemperatureTiers> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("very_cold").forGetter(TemperatureTiers::veryCold),
            Codec.INT.fieldOf("cold").forGetter(TemperatureTiers::cold),
            Codec.INT.fieldOf("normal").forGetter(TemperatureTiers::normal),
            Codec.INT.fieldOf("hot").forGetter(TemperatureTiers::hot),
            Codec.INT.fieldOf("very_hot").forGetter(TemperatureTiers::veryHot)
    ).apply(instance, TemperatureTiers::new));

    public static final Codec<TemperatureTiers> CODEC = Codec.either(Codec.INT, OBJECT_CODEC).xmap(either -> either.map(TemperatureTiers::uniform, Function.identity()), Either::right);

    public static TemperatureTiers uniform(int value) {
        return new TemperatureTiers(value, value, value, value, value);
    }

    public int get(EnvironmentCode environmentCode) {
        return switch (environmentCode) {
            case VERY_COLD -> this.veryCold;
            case COLD -> this.cold;
            case NORMAL -> this.normal;
            case HOT -> this.hot;
            case VERY_HOT -> this.veryHot;
        };
    }
}
