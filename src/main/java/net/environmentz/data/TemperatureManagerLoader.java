package net.environmentz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.environmentz.EnvironmentzMain;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.data.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

public class TemperatureManagerLoader extends TemperatureDataLoader {

    private static final String EFFECT_SECTION = "effect";
    private static final Set<String> SECTIONS = Set.of("body_temperature", "body_wetness", "body_protection", "biome_temperature", "thermometer_temperature", "acclimatization", "room_heat",
            EFFECT_SECTION);

    @Override
    public Identifier getFabricId() {
        return EnvironmentzMain.identifierOf("temperature_manager_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Identifier, DimensionTemperatures> dimensions = new HashMap<>();
        Map<StatusEffect, EffectTemperature> effects = new HashMap<>();
        Set<String> replacedSections = new HashSet<>();
        Set<String> replacedEffects = new HashSet<>();
        Set<String> replacedDimensions = new HashSet<>();

        forEachJsonFile(manager, "manager", (file, data) -> {
            loadSection(data, "body_temperature", BodyTemperature.CODEC, file, replacedSections, Temperatures::setBodyTemperature);
            loadSection(data, "body_wetness", BodyWetness.CODEC, file, replacedSections, Temperatures::setBodyWetness);
            loadSection(data, "body_protection", BodyProtection.CODEC, file, replacedSections, Temperatures::setBodyProtection);
            loadSection(data, "biome_temperature", BiomeTemperature.CODEC, file, replacedSections, Temperatures::setBiomeTemperature);
            loadSection(data, "thermometer_temperature", ThermometerTemperature.CODEC, file, replacedSections, Temperatures::setThermometerTemperature);
            loadSection(data, "acclimatization", Acclimatization.CODEC, file, replacedSections, Temperatures::setAcclimatization);
            loadSection(data, "room_heat", RoomHeat.CODEC, file, replacedSections, Temperatures::setRoomHeat);

            if (data.get(EFFECT_SECTION) instanceof JsonObject effectObject && !replacedSections.contains(EFFECT_SECTION)) {
                loadEffects(effectObject, file, replacedEffects, effects);
                if (isReplacing(effectObject)) {
                    replacedSections.add(EFFECT_SECTION);
                }
            }

            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                if (SECTIONS.contains(key) || !(entry.getValue() instanceof JsonObject) || replacedDimensions.contains(key)) {
                    continue;
                }
                Identifier dimensionIdentifier = Identifier.tryParse(key);
                if (dimensionIdentifier == null) {
                    EnvironmentzMain.LOGGER.info("{} is not a valid dimension identifier", key);
                    continue;
                }
                if (isReplacing(entry.getValue())) {
                    replacedDimensions.add(key);
                }
                parse(DimensionTemperatures.CODEC, entry.getValue(), file, key).ifPresent(dimension -> dimensions.put(dimensionIdentifier, dimension));
            }
        });

        Temperatures.setDimensions(dimensions);
        Temperatures.setEffects(effects);
    }

    private static <T> void loadSection(JsonObject data, String key, Codec<T> codec, Identifier file, Set<String> replacedSections, Consumer<T> setter) {
        if (!(data.get(key) instanceof JsonObject jsonObject) || replacedSections.contains(key)) {
            return;
        }
        parse(codec, jsonObject, file, key).ifPresent(setter);
        if (isReplacing(jsonObject)) {
            replacedSections.add(key);
        }
    }

    private static void loadEffects(JsonObject effectObject, Identifier file, Set<String> replacedEffects, Map<StatusEffect, EffectTemperature> effects) {
        for (Map.Entry<String, JsonElement> entry : effectObject.entrySet()) {
            String key = entry.getKey();
            if (!(entry.getValue() instanceof JsonObject) || replacedEffects.contains(key)) {
                continue;
            }
            if (isReplacing(entry.getValue())) {
                replacedEffects.add(key);
            }
            Identifier effectIdentifier = Identifier.tryParse(key);
            Optional<StatusEffect> effect = effectIdentifier == null ? Optional.empty() : Registries.STATUS_EFFECT.getOrEmpty(effectIdentifier);
            if (effect.isEmpty()) {
                EnvironmentzMain.LOGGER.info("{} is not a valid effect identifier", key);
                continue;
            }
            parse(EffectTemperature.CODEC, entry.getValue(), file, key).ifPresent(effectTemperature -> effects.put(effect.get(), effectTemperature));
        }
    }
}
