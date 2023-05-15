package net.environmentz.temperature;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

public class Temperatures {

    public static final Identifier OVERWORLD = new Identifier("minecraft:overworld");

    // body temperature
    private static int body_temperature_max_very_cold;
    private static int body_temperature_max_cold;
    private static int body_temperature_min_cold;
    private static int body_temperature_normal;
    private static int body_temperature_min_hot;
    private static int body_temperature_max_hot;
    private static int body_temperature_max_very_hot;

    // body wetness
    private static int body_wetness_max;
    private static int body_wetness_soaked;
    private static int body_wetness_water;
    private static int body_wetness_rain;
    private static int body_wetness_dry;

    // body protection
    private static int body_heat_protection;
    private static int body_cold_protection;
    private static int body_heat_resistance;
    private static int body_cold_resistance;

    // biome temperature
    private static float biome_temperature_very_cold;
    private static float biome_temperature_cold;
    private static float biome_temperature_hot;
    private static float biome_temperature_very_hot;

    // thermometer temperature
    private static int thermometer_very_cold;
    private static int thermometer_cold;
    private static int thermometer_hot;
    private static int thermometer_very_hot;

    // acclimatization
    private static int hot_body_acclimatization_temperature;
    private static int hot_body_acclimatization;
    private static int very_hot_body_acclimatization_temperature;
    private static int very_hot_body_acclimatization;
    private static int cold_body_acclimatization_temperature;
    private static int cold_body_acclimatization;
    private static int very_cold_body_acclimatization_temperature;
    private static int very_cold_body_acclimatization;

    // dimension standard temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionStandardTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension day temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionDayTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension night temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionNightTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension armor temperature
    private static HashMap<Identifier, HashMap<Integer, Float>> dimensionArmorTemperatures = new HashMap<Identifier, HashMap<Integer, Float>>();
    // dimension insolated armor temperature
    private static HashMap<Identifier, HashMap<Integer, Float>> dimensionInsulatedArmorTemperatures = new HashMap<Identifier, HashMap<Integer, Float>>();
    // dimension iced armor temperature
    private static HashMap<Identifier, HashMap<Integer, Float>> dimensionIcedArmorTemperatures = new HashMap<Identifier, HashMap<Integer, Float>>();
    // dimension soaked temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionSoakedTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension wett temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionWettTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension sweat temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionSweatTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension shadow temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionShadowTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension height temperature
    private static HashMap<Identifier, HashMap<Integer, Integer>> dimensionHeightTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();
    // dimension acclimatization
    private static HashMap<Identifier, Integer> dimensionAcclimatization = new HashMap<Identifier, Integer>();

    // block
    private static HashMap<Integer, HashMap<Integer, Integer>> blockTemperatures = new HashMap<Integer, HashMap<Integer, Integer>>();
    private static HashMap<Integer, BooleanProperty> blockProperties = new HashMap<Integer, BooleanProperty>();
    // fluid
    private static HashMap<Integer, HashMap<Integer, Integer>> fluidTemperatures = new HashMap<Integer, HashMap<Integer, Integer>>();
    // item
    private static HashMap<Integer, HashMap<Integer, Integer>> itemTemperatures = new HashMap<Integer, HashMap<Integer, Integer>>();

    // effect
    private static HashMap<Identifier, HashMap<Integer, Integer>> effectTemperatures = new HashMap<Identifier, HashMap<Integer, Integer>>();

    // Setters
    public static void setBodyTemperatures(int max_very_cold, int max_cold, int min_cold, int normal, int min_hot, int max_hot, int max_very_hot) {
        body_temperature_max_very_cold = max_very_cold;
        body_temperature_max_cold = max_cold;
        body_temperature_min_cold = min_cold;
        body_temperature_normal = normal;
        body_temperature_min_hot = min_hot;
        body_temperature_max_hot = max_hot;
        body_temperature_max_very_hot = max_very_hot;
    }

    public static void setBodyWetness(int wetness_max, int wetness_soaked, int wetness_water, int wetness_rain, int wetness_dry) {
        body_wetness_max = wetness_max;
        body_wetness_soaked = wetness_soaked;
        body_wetness_water = wetness_water;
        body_wetness_rain = wetness_rain;
        body_wetness_dry = wetness_dry;
    }

    public static void setBodyProtection(int max_heat, int max_cold, int max_heat_resistance, int max_cold_resistance) {
        body_heat_protection = max_heat;
        body_cold_protection = max_cold;
        body_heat_resistance = max_heat_resistance;
        body_cold_resistance = max_cold_resistance;
    }

    public static void setBiomeTemperatures(float very_cold, float cold, float hot, float very_hot) {
        biome_temperature_very_cold = very_cold;
        biome_temperature_cold = cold;
        biome_temperature_hot = hot;
        biome_temperature_very_hot = very_hot;
    }

    public static void setThermometerTemperatures(int very_cold, int cold, int hot, int very_hot) {
        thermometer_very_cold = very_cold;
        thermometer_cold = cold;
        thermometer_hot = hot;
        thermometer_very_hot = very_hot;
    }

    public static void setAcclimatizationTemperatures(int hot_body_temperature, int hot_body, int very_hot_body_temperature, int very_hot_body, int cold_body_temperature, int cold_body,
            int very_cold_body_temperature, int very_cold_body) {
        hot_body_acclimatization_temperature = hot_body_temperature;
        hot_body_acclimatization = hot_body;
        very_hot_body_acclimatization_temperature = very_hot_body_temperature;
        very_hot_body_acclimatization = very_hot_body;
        cold_body_acclimatization_temperature = cold_body_temperature;
        cold_body_acclimatization = cold_body;
        very_cold_body_acclimatization_temperature = very_cold_body_temperature;
        very_cold_body_acclimatization = very_cold_body;
    }

    public static void setDimensionStandardTemperatures(Identifier dimensionIdentifier, int very_cold, int cold, int normal, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionStandardTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionDayTemperatures(Identifier dimensionIdentifier, int very_cold, int cold, int normal, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionDayTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionNightTemperatures(Identifier dimensionIdentifier, int very_cold, int cold, int normal, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionNightTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionArmorTemperatures(Identifier dimensionIdentifier, float very_cold, float cold, float normal, float hot, float very_hot) {
        HashMap<Integer, Float> hashMap = new HashMap<Integer, Float>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionArmorTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionInsulatedArmorTemperatures(Identifier dimensionIdentifier, float very_cold, float cold, float normal, float hot, float very_hot) {
        HashMap<Integer, Float> hashMap = new HashMap<Integer, Float>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionInsulatedArmorTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionIcedArmorTemperatures(Identifier dimensionIdentifier, float very_cold, float cold, float normal, float hot, float very_hot) {
        HashMap<Integer, Float> hashMap = new HashMap<Integer, Float>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionIcedArmorTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionSoakedTemperatures(Identifier dimensionIdentifier, int very_cold, int cold, int normal, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionSoakedTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionWettTemperatures(Identifier dimensionIdentifier, int very_cold, int cold, int normal, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionWettTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionSweatTemperatures(Identifier dimensionIdentifier, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, hot);
        hashMap.put(1, very_hot);
        dimensionSweatTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionShadowTemperatures(Identifier dimensionIdentifier, int very_cold, int cold, int normal, int hot, int very_hot) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_cold);
        hashMap.put(1, cold);
        hashMap.put(2, normal);
        hashMap.put(3, hot);
        hashMap.put(4, very_hot);
        dimensionShadowTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionHeightTemperatures(Identifier dimensionIdentifier, int very_low, int low, int high, int very_high, int very_low_height, int low_height, int high_height,
            int very_high_height) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        hashMap.put(0, very_low);
        hashMap.put(1, low);
        hashMap.put(2, high);
        hashMap.put(3, very_high);
        hashMap.put(4, very_low_height);
        hashMap.put(5, low_height);
        hashMap.put(6, high_height);
        hashMap.put(7, very_high_height);
        dimensionHeightTemperatures.put(dimensionIdentifier, hashMap);
    }

    public static void setDimensionAcclimatization(Identifier dimensionIdentifier, int acclimatization) {
        dimensionAcclimatization.put(dimensionIdentifier, acclimatization);
    }

    public static void setBlockTemperatures(int blockId, HashMap<Integer, Integer> map) {
        blockTemperatures.put(blockId, map);
    }

    public static void setBlockProperty(int blockId, String property) {
        blockProperties.put(blockId, BooleanProperty.of(property));
    }

    public static void setFluidTemperatures(int fluidId, HashMap<Integer, Integer> map) {
        fluidTemperatures.put(fluidId, map);
    }

    public static void setItemTemperatures(int itemId, HashMap<Integer, Integer> map) {
        itemTemperatures.put(itemId, map);
    }

    public static void setEffectTemperatures(Identifier effectIdentifier, HashMap<Integer, Integer> map) {
        effectTemperatures.put(effectIdentifier, map);
    }

    // Getters
    public static int getBodyTemperatures(int environmentCode) { // environmentCode 0: max_very_cold, 1: max_cold, 2: min_cold, 3: normal, 4: min_hot, 5: max_hot, 6: max_very_hot
        switch (environmentCode) {
        case 0:
            return body_temperature_max_very_cold;
        case 1:
            return body_temperature_max_cold;
        case 2:
            return body_temperature_min_cold;
        case 3:
            return body_temperature_normal;
        case 4:
            return body_temperature_min_hot;
        case 5:
            return body_temperature_max_hot;
        case 6:
            return body_temperature_max_very_hot;

        default:
            return 0;
        }
    }

    public static int getBodyWetness(int code) { // code 0: wetness_max, 1: wetness_soaked 2: wetness_water, 3: wetness_rain 4: wetness_dry
        switch (code) {
        case 0:
            return body_wetness_max;
        case 1:
            return body_wetness_soaked;
        case 2:
            return body_wetness_water;
        case 3:
            return body_wetness_rain;
        case 4:
            return body_wetness_dry;
        default:
            return 0;
        }
    }

    public static int getBodyProtection(int code) { // code 0: max_heat, 1: max_cold 2: max_heat_resistance, 3: max_cold_resistance
        switch (code) {
        case 0:
            return body_heat_protection;
        case 1:
            return body_cold_protection;
        case 2:
            return body_heat_resistance;
        case 3:
            return body_cold_resistance;
        default:
            return 0;
        }
    }

    public static float getBiomeTemperatures(int environmentCode) { // environmentCode 0:very_cold 1: cold, 2: hot, 3: very_hot
        switch (environmentCode) {
        case 0:
            return biome_temperature_very_cold;
        case 1:
            return biome_temperature_cold;
        case 2:
            return biome_temperature_hot;
        case 3:
            return biome_temperature_very_hot;
        default:
            return 0;
        }
    }

    public static int getThermometerTemperatures(int environmentCode) { // environmentCode 0:very_cold 1: cold, 2: hot, 3: very_hot
        switch (environmentCode) {
        case 0:
            return thermometer_very_cold;
        case 1:
            return thermometer_cold;
        case 2:
            return thermometer_hot;
        case 3:
            return thermometer_very_hot;
        default:
            return 0;
        }
    }

    public static int getAcclimatization(int code) { // code 0: hot temperature 1: hot acclimatization, 2: very_hot, 3: very_hot, 4: cold, 5: cold, 6: very_cold, 7: very_cold
        switch (code) {
        case 0:
            return hot_body_acclimatization_temperature;
        case 1:
            return hot_body_acclimatization;
        case 2:
            return very_hot_body_acclimatization_temperature;
        case 3:
            return very_hot_body_acclimatization;
        case 4:
            return cold_body_acclimatization_temperature;
        case 5:
            return cold_body_acclimatization;
        case 6:
            return very_cold_body_acclimatization_temperature;
        case 7:
            return very_cold_body_acclimatization;
        default:
            return 0;
        }
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static int getDimensionStandardTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionStandardTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionStandardTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static int getDimensionDayTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionDayTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionDayTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static int getDimensionNightTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionNightTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionNightTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static float getDimensionArmorTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionArmorTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionArmorTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static float getDimensionInsulatedArmorTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionInsulatedArmorTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionInsulatedArmorTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static float getDimensionIcedArmorTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionIcedArmorTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionIcedArmorTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static int getDimensionSoakedTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionSoakedTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionSoakedTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static int getDimensionWettTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionWettTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionWettTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: hot, 1: very_hot
    public static int getDimensionSweatTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionSweatTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionSweatTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_cold, 1: cold, 2: normal, 3: hot, 4: very_hot
    public static int getDimensionShadowTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionShadowTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionShadowTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    // environmentCode 0: very_low, 1: low, 2: high, 3: very_high, 4: very_low_height, 5: low_height, 6: high_height, 7: very_high_height
    public static int getDimensionHeightTemperatures(Identifier dimensionIdentifier, int environmentCode) {
        if (dimensionHeightTemperatures.containsKey(dimensionIdentifier)) {
            return dimensionHeightTemperatures.get(dimensionIdentifier).get(environmentCode);
        } else
            return 0;
    }

    public static int getDimensionAcclimatization(Identifier dimensionIdentifier) {
        if (dimensionAcclimatization.containsKey(dimensionIdentifier)) {
            return dimensionAcclimatization.get(dimensionIdentifier);
        } else
            return 1997;
    }

    // distance -1 = max_count
    public static int getBlockTemperature(int blockId, int distance) {
        if (blockTemperatures.get(blockId).containsKey(distance))
            return blockTemperatures.get(blockId).get(distance);
        else
            return 0;
    }

    @Nullable
    public static BooleanProperty getBlockProperty(int blockId) {
        return blockProperties.get(blockId);
    }

    // distance -1 = max_count
    public static int getFluidTemperature(int fluidId, int distance) {
        if (fluidTemperatures.get(fluidId).containsKey(distance))
            return fluidTemperatures.get(fluidId).get(distance);
        else
            return 0;
    }

    // code -1 = damage, 0 = temperature, 1 = heat_protection, 2 = cold_protection
    public static int getItemValue(int itemId, int code) {
        if (itemTemperatures.get(itemId).containsKey(code))
            return itemTemperatures.get(itemId).get(code);
        else
            return 0;
    }

    // 0 = temperature, 1 = heat_protection, 2 = cold_protection
    public static int getEffectValue(Identifier effectIdentifier, int code) {
        if (effectTemperatures.containsKey(effectIdentifier))
            return effectTemperatures.get(effectIdentifier).get(code);
        else
            return 0;
    }

    public static boolean shouldUseOverworldTemperatures(Identifier dimensionIdentifier) {
        if (getDimensionStandardTemperatures(dimensionIdentifier, 0) == 0 && getDimensionStandardTemperatures(dimensionIdentifier, 4) == 0 && getDimensionDayTemperatures(dimensionIdentifier, 0) == 0
                && getDimensionDayTemperatures(dimensionIdentifier, 4) == 0) {
            return true;
        }
        return false;
    }

    public static boolean shouldUseStandardTemperatures(Identifier dimensionIdentifier) {
        if (getDimensionDayTemperatures(dimensionIdentifier, 0) == 0 && getDimensionDayTemperatures(dimensionIdentifier, 4) == 0) {
            return true;
        }
        return false;
    }

    public static boolean hasBlockTemperature(int blockId) {
        if (blockTemperatures.containsKey(blockId)) {
            return true;
        }
        return false;
    }

    public static boolean hasBlockProperty(int blockId) {
        if (blockProperties.containsKey(blockId)) {
            return true;
        }
        return false;
    }

    public static boolean hasFluidTemperature(int fluidId) {
        if (fluidTemperatures.containsKey(fluidId)) {
            return true;
        }
        return false;
    }

    public static boolean hasItemTemperature(int itemId) {
        if (itemTemperatures.containsKey(itemId)) {
            return true;
        }
        return false;
    }

    public static boolean hasEffectTemperature(Identifier effectIdentifier) {
        if (effectTemperatures.containsKey(effectIdentifier)) {
            return true;
        }
        return false;
    }

}
