package net.environmentz.temperature.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record DimensionTemperatures(TemperatureTiers standard, TemperatureTiers day, TemperatureTiers night, ArmorTiers armor, ArmorTiers insulatedArmor, ArmorTiers icedArmor,
                                    TemperatureTiers soaked, TemperatureTiers wett, SweatTiers sweat, TemperatureTiers shadow, HeightTemperatures height, Optional<Integer> acclimatization) {

    public static final DimensionTemperatures EMPTY = new DimensionTemperatures(TemperatureTiers.ZERO, TemperatureTiers.ZERO, TemperatureTiers.ZERO, ArmorTiers.ZERO, ArmorTiers.ZERO, ArmorTiers.ZERO,
            TemperatureTiers.ZERO, TemperatureTiers.ZERO, SweatTiers.ZERO, TemperatureTiers.ZERO, HeightTemperatures.ZERO, Optional.empty());

    public static final Codec<DimensionTemperatures> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TemperatureTiers.CODEC.optionalFieldOf("standard").forGetter(dimension -> Optional.of(dimension.standard())),
            TemperatureTiers.CODEC.optionalFieldOf("day").forGetter(dimension -> Optional.of(dimension.day())),
            TemperatureTiers.CODEC.optionalFieldOf("night").forGetter(dimension -> Optional.of(dimension.night())),
            ArmorTiers.CODEC.fieldOf("armor").forGetter(DimensionTemperatures::armor),
            ArmorTiers.CODEC.fieldOf("insulated_armor").forGetter(DimensionTemperatures::insulatedArmor),
            ArmorTiers.CODEC.fieldOf("iced_armor").forGetter(DimensionTemperatures::icedArmor),
            TemperatureTiers.CODEC.fieldOf("soaked").forGetter(DimensionTemperatures::soaked),
            TemperatureTiers.CODEC.fieldOf("wett").forGetter(DimensionTemperatures::wett),
            SweatTiers.CODEC.fieldOf("sweat").forGetter(DimensionTemperatures::sweat),
            TemperatureTiers.CODEC.fieldOf("shadow").forGetter(DimensionTemperatures::shadow),
            HeightTemperatures.CODEC.fieldOf("height").forGetter(DimensionTemperatures::height),
            Codec.INT.optionalFieldOf("acclimatization").forGetter(DimensionTemperatures::acclimatization)
    ).apply(instance, DimensionTemperatures::create));

    private static DimensionTemperatures create(Optional<TemperatureTiers> standard, Optional<TemperatureTiers> day, Optional<TemperatureTiers> night, ArmorTiers armor, ArmorTiers insulatedArmor,
                                                ArmorTiers icedArmor, TemperatureTiers soaked, TemperatureTiers wett, SweatTiers sweat, TemperatureTiers shadow, HeightTemperatures height,
                                                Optional<Integer> acclimatization) {
        boolean hasDayCycle = day.isPresent() && night.isPresent();
        return new DimensionTemperatures(standard.orElse(TemperatureTiers.ZERO), hasDayCycle ? day.get() : TemperatureTiers.ZERO, hasDayCycle ? night.get() : TemperatureTiers.ZERO, armor,
                insulatedArmor, icedArmor, soaked, wett, sweat, shadow, height, acclimatization);
    }

    public boolean usesStandardTemperatures() {
        return this.day.veryCold() == 0 && this.day.veryHot() == 0;
    }

    public boolean isUnset() {
        return this.standard.veryCold() == 0 && this.standard.veryHot() == 0 && usesStandardTemperatures();
    }
}
