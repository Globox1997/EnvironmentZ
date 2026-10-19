package net.environmentz.temperature;

import net.environmentz.temperature.data.*;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class Temperatures {

    public static final Identifier OVERWORLD = Identifier.ofVanilla("overworld");

    private static BodyTemperature bodyTemperature = BodyTemperature.EMPTY;
    private static BodyWetness bodyWetness = BodyWetness.EMPTY;
    private static BodyProtection bodyProtection = BodyProtection.EMPTY;
    private static BiomeTemperature biomeTemperature = BiomeTemperature.EMPTY;
    private static ThermometerTemperature thermometerTemperature = ThermometerTemperature.EMPTY;
    private static Acclimatization acclimatization = Acclimatization.EMPTY;
    private static RoomHeat roomHeat = RoomHeat.DEFAULT;

    private static Map<Identifier, DimensionTemperatures> dimensions = Map.of();
    private static Map<StatusEffect, EffectTemperature> effects = Map.of();
    private static Map<Block, BlockTemperature> blocks = Map.of();
    private static Map<Fluid, BlockTemperature> fluids = Map.of();
    private static Map<Item, ItemTemperature> items = Map.of();

    private Temperatures() {
    }

    public static BodyTemperature getBodyTemperature() {
        return bodyTemperature;
    }

    public static void setBodyTemperature(BodyTemperature value) {
        bodyTemperature = value;
    }

    public static BodyWetness getBodyWetness() {
        return bodyWetness;
    }

    public static void setBodyWetness(BodyWetness value) {
        bodyWetness = value;
    }

    public static BodyProtection getBodyProtection() {
        return bodyProtection;
    }

    public static void setBodyProtection(BodyProtection value) {
        bodyProtection = value;
    }

    public static BiomeTemperature getBiomeTemperature() {
        return biomeTemperature;
    }

    public static void setBiomeTemperature(BiomeTemperature value) {
        biomeTemperature = value;
    }

    public static ThermometerTemperature getThermometerTemperature() {
        return thermometerTemperature;
    }

    public static void setThermometerTemperature(ThermometerTemperature value) {
        thermometerTemperature = value;
    }

    public static Acclimatization getAcclimatization() {
        return acclimatization;
    }

    public static void setAcclimatization(Acclimatization value) {
        acclimatization = value;
    }

    public static RoomHeat getRoomHeat() {
        return roomHeat;
    }

    public static void setRoomHeat(RoomHeat value) {
        roomHeat = value;
    }

    public static void setDimensions(Map<Identifier, DimensionTemperatures> value) {
        dimensions = Map.copyOf(value);
    }

    public static void setEffects(Map<StatusEffect, EffectTemperature> value) {
        effects = Map.copyOf(value);
    }

    public static void setBlocks(Map<Block, BlockTemperature> blockValues, Map<Fluid, BlockTemperature> fluidValues) {
        blocks = Map.copyOf(blockValues);
        fluids = Map.copyOf(fluidValues);
    }

    public static void setItems(Map<Item, ItemTemperature> value) {
        items = Map.copyOf(value);
    }

    @Nullable
    public static DimensionTemperatures getRawDimension(Identifier dimensionIdentifier) {
        return dimensions.get(dimensionIdentifier);
    }

    public static DimensionTemperatures getDimension(Identifier dimensionIdentifier) {
        DimensionTemperatures dimension = dimensions.get(dimensionIdentifier);
        if (dimension == null || dimension.isUnset()) {
            return dimensions.getOrDefault(OVERWORLD, DimensionTemperatures.EMPTY);
        }
        return dimension;
    }

    @Nullable
    public static EffectTemperature getEffectTemperature(StatusEffect effect) {
        return effects.get(effect);
    }

    @Nullable
    public static BlockTemperature getBlockTemperature(Block block) {
        return blocks.get(block);
    }

    @Nullable
    public static BlockTemperature getFluidTemperature(Fluid fluid) {
        return fluids.get(fluid);
    }

    @Nullable
    public static ItemTemperature getItemTemperature(Item item) {
        return items.get(item);
    }
}
