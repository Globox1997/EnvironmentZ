package net.environmentz.temperature.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record SweatTiers(int hot, int veryHot) {

    public static final SweatTiers ZERO = new SweatTiers(0, 0);

    private static final Codec<SweatTiers> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hot").forGetter(SweatTiers::hot),
            Codec.INT.fieldOf("very_hot").forGetter(SweatTiers::veryHot)
    ).apply(instance, SweatTiers::new));

    public static final Codec<SweatTiers> CODEC = Codec.either(Codec.INT, OBJECT_CODEC).xmap(either -> either.map(value -> new SweatTiers(value, value), Function.identity()), Either::right);

    public int get(EnvironmentCode environmentCode) {
        return environmentCode == EnvironmentCode.VERY_HOT ? this.veryHot : this.hot;
    }
}
