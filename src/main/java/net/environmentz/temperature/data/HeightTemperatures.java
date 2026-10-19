package net.environmentz.temperature.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record HeightTemperatures(int veryLow, int low, int high, int veryHigh, int veryLowHeight, int lowHeight, int highHeight, int veryHighHeight) {

    public static final HeightTemperatures ZERO = uniform(0);

    private static final Codec<HeightTemperatures> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("very_low").forGetter(HeightTemperatures::veryLow),
            Codec.INT.fieldOf("low").forGetter(HeightTemperatures::low),
            Codec.INT.fieldOf("high").forGetter(HeightTemperatures::high),
            Codec.INT.fieldOf("very_high").forGetter(HeightTemperatures::veryHigh),
            Codec.INT.fieldOf("very_low_height").forGetter(HeightTemperatures::veryLowHeight),
            Codec.INT.fieldOf("low_height").forGetter(HeightTemperatures::lowHeight),
            Codec.INT.fieldOf("high_height").forGetter(HeightTemperatures::highHeight),
            Codec.INT.fieldOf("very_high_height").forGetter(HeightTemperatures::veryHighHeight)
    ).apply(instance, HeightTemperatures::new));

    public static final Codec<HeightTemperatures> CODEC = Codec.either(Codec.INT, OBJECT_CODEC).xmap(either -> either.map(HeightTemperatures::uniform, Function.identity()), Either::right);

    public static HeightTemperatures uniform(int value) {
        return new HeightTemperatures(value, value, value, value, value, value, value, value);
    }

    public int getTemperature(int y) {
        if (y < this.lowHeight) {
            return y < this.veryLowHeight ? this.veryLow : this.low;
        }
        if (y > this.highHeight) {
            return y > this.veryHighHeight ? this.veryHigh : this.high;
        }
        return 0;
    }
}
