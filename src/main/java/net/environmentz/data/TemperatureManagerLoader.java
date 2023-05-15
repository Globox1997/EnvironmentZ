package net.environmentz.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.environmentz.EnvironmentzMain;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class TemperatureManagerLoader implements SimpleSynchronousResourceReloadListener {

    // List to store replacing bools
    private ArrayList<String> replaceList = new ArrayList<String>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("environmentz", "temperature_manager_loader");
    }

    @Override
    public void reload(ResourceManager manager) {

        manager.findResources("manager", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                if (data.has("body_temperature")) {
                    JsonElement bodyTemperatureElement = data.get("body_temperature");
                    if (bodyTemperatureElement != null && bodyTemperatureElement instanceof JsonObject) {
                        JsonObject bodyTemperatureObject = (JsonObject) bodyTemperatureElement;
                        if (!replaceList.contains("body_temperature")) {
                            Temperatures.setBodyTemperatures(bodyTemperatureObject.get("max_very_cold").getAsInt(), bodyTemperatureObject.get("max_cold").getAsInt(),
                                    bodyTemperatureObject.get("min_cold").getAsInt(), bodyTemperatureObject.get("normal").getAsInt(), bodyTemperatureObject.get("min_hot").getAsInt(),
                                    bodyTemperatureObject.get("max_hot").getAsInt(), bodyTemperatureObject.get("max_very_hot").getAsInt());
                            if (JsonHelper.getBoolean(bodyTemperatureObject, "replace", false)) {
                                replaceList.add("body_temperature");
                            }
                        }
                    }
                }
                if (data.has("body_wetness")) {
                    JsonElement bodyWetnessElement = data.get("body_wetness");
                    if (bodyWetnessElement != null && bodyWetnessElement instanceof JsonObject) {
                        JsonObject bodyWetnessObject = (JsonObject) bodyWetnessElement;
                        if (!replaceList.contains("body_wetness")) {
                            Temperatures.setBodyWetness(bodyWetnessObject.get("max_wetness").getAsInt(), bodyWetnessObject.get("soaked").getAsInt(), bodyWetnessObject.get("water").getAsInt(),
                                    bodyWetnessObject.get("rain").getAsInt(), bodyWetnessObject.get("dry").getAsInt());
                            if (JsonHelper.getBoolean(bodyWetnessObject, "replace", false)) {
                                replaceList.add("body_wetness");
                            }
                        }
                    }
                }
                if (data.has("body_protection")) {
                    JsonElement bodyProtectionElement = data.get("body_protection");
                    if (bodyProtectionElement != null && bodyProtectionElement instanceof JsonObject) {
                        JsonObject bodyProtectionObject = (JsonObject) bodyProtectionElement;
                        if (!replaceList.contains("body_protection")) {
                            Temperatures.setBodyProtection(bodyProtectionObject.get("max_heat").getAsInt(), bodyProtectionObject.get("max_cold").getAsInt(),
                                    bodyProtectionObject.get("max_heat_resistance").getAsInt(), bodyProtectionObject.get("max_cold_resistance").getAsInt());
                            if (JsonHelper.getBoolean(bodyProtectionObject, "replace", false)) {
                                replaceList.add("body_protection");
                            }
                        }
                    }
                }
                if (data.has("biome_temperature")) {
                    JsonElement biomeTemperatureElement = data.get("biome_temperature");
                    if (biomeTemperatureElement != null && biomeTemperatureElement instanceof JsonObject) {
                        JsonObject biomeTemperatureObject = (JsonObject) biomeTemperatureElement;
                        if (!replaceList.contains("biome_temperature")) {
                            Temperatures.setBiomeTemperatures(biomeTemperatureObject.get("very_cold").getAsFloat(), biomeTemperatureObject.get("cold").getAsFloat(),
                                    biomeTemperatureObject.get("hot").getAsFloat(), biomeTemperatureObject.get("very_hot").getAsFloat());
                            if (JsonHelper.getBoolean(biomeTemperatureObject, "replace", false)) {
                                replaceList.add("biome_temperature");
                            }
                        }
                    }
                }
                if (data.has("thermometer_temperature")) {
                    JsonElement thermometerTemperatureElement = data.get("thermometer_temperature");
                    if (thermometerTemperatureElement != null && thermometerTemperatureElement instanceof JsonObject) {
                        JsonObject thermometerTemperatureObject = (JsonObject) thermometerTemperatureElement;
                        if (!replaceList.contains("thermometer_temperature")) {
                            Temperatures.setThermometerTemperatures(thermometerTemperatureObject.get("very_cold").getAsInt(), thermometerTemperatureObject.get("cold").getAsInt(),
                                    thermometerTemperatureObject.get("hot").getAsInt(), thermometerTemperatureObject.get("very_hot").getAsInt());
                            if (JsonHelper.getBoolean(thermometerTemperatureObject, "replace", false)) {
                                replaceList.add("thermometer_temperature");
                            }
                        }
                    }
                }
                if (data.has("acclimatization")) {
                    JsonElement acclimatizationElement = data.get("acclimatization");
                    if (acclimatizationElement != null && acclimatizationElement instanceof JsonObject) {
                        JsonObject acclimatizationObject = (JsonObject) acclimatizationElement;
                        if (!replaceList.contains("acclimatization")) {
                            Temperatures.setAcclimatizationTemperatures(acclimatizationObject.get("hot_body_temperature").getAsInt(), acclimatizationObject.get("hot_body").getAsInt(),
                                    acclimatizationObject.get("very_hot_body_temperature").getAsInt(), acclimatizationObject.get("very_hot_body").getAsInt(),
                                    acclimatizationObject.get("cold_body_temperature").getAsInt(), acclimatizationObject.get("cold_body").getAsInt(),
                                    acclimatizationObject.get("very_cold_body_temperature").getAsInt(), acclimatizationObject.get("very_cold_body").getAsInt());
                            if (JsonHelper.getBoolean(acclimatizationObject, "replace", false)) {
                                replaceList.add("acclimatization");
                            }
                        }
                    }
                }
                if (data.has("effect")) {
                    JsonElement effectElement = data.get("effect");
                    if (effectElement != null && effectElement instanceof JsonObject) {
                        JsonObject effectObject = (JsonObject) effectElement;
                        if (!replaceList.contains("effect")) {
                            Iterator<String> iterator = effectObject.keySet().iterator();
                            while (iterator.hasNext()) {
                                String keyString = iterator.next();
                                JsonElement jsonElement = effectObject.get(keyString);
                                if (jsonElement != null && jsonElement instanceof JsonObject) {
                                    JsonObject jsonObject = (JsonObject) jsonElement;
                                    if (!replaceList.contains(keyString)) {
                                        if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                                            replaceList.add(keyString);
                                        }
                                        Identifier effectIdentifier = new Identifier(keyString);
                                        if (Registry.STATUS_EFFECT.get(effectIdentifier) == null) {
                                            EnvironmentzMain.LOGGER.info("{} is not a valid effect identifier", effectIdentifier);
                                            continue;
                                        }
                                        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
                                        hashMap.put(0, jsonObject.get("temperature").getAsInt());
                                        if (jsonObject.has("heat_protection")) {
                                            hashMap.put(1, jsonObject.get("heat_protection").getAsInt());
                                        } else {
                                            hashMap.put(1, 0);
                                        }
                                        if (jsonObject.has("cold_protection")) {
                                            hashMap.put(2, jsonObject.get("cold_protection").getAsInt());
                                        } else {
                                            hashMap.put(2, 0);
                                        }
                                        Temperatures.setEffectTemperatures(effectIdentifier, hashMap);
                                    }
                                }
                            }

                            if (JsonHelper.getBoolean(effectObject, "replace", false)) {
                                replaceList.add("effect");
                            }
                        }
                    }
                }
                Iterator<String> iterator = data.keySet().iterator();
                while (iterator.hasNext()) {
                    String keyString = iterator.next();
                    if (keyString.equals("body_temperature") || keyString.equals("body_wetness") || keyString.equals("body_protection") || keyString.equals("biome_temperature")
                            || keyString.equals("thermometer_temperature") || keyString.equals("acclimatization") || keyString.equals("effect")) {
                        continue;
                    }

                    JsonElement jsonElement = data.get(keyString);
                    if (jsonElement != null && jsonElement instanceof JsonObject) {
                        JsonObject jsonObject = (JsonObject) jsonElement;
                        if (!replaceList.contains(keyString)) {
                            if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                                replaceList.add(keyString);
                            }
                            Identifier dimensionIdentifier = new Identifier(keyString);

                            if (JsonHelper.getBoolean(jsonObject, "basic", false)) {
                                if (jsonObject.has("standard")) {
                                    Temperatures.setDimensionStandardTemperatures(dimensionIdentifier, jsonObject.get("standard").getAsInt(), jsonObject.get("standard").getAsInt(),
                                            jsonObject.get("standard").getAsInt(), jsonObject.get("standard").getAsInt(), jsonObject.get("standard").getAsInt());
                                } else {
                                    Temperatures.setDimensionStandardTemperatures(dimensionIdentifier, 0, 0, 0, 0, 0);
                                }
                                if (jsonObject.has("day") && jsonObject.has("night")) {
                                    Temperatures.setDimensionDayTemperatures(dimensionIdentifier, jsonObject.get("day").getAsInt(), jsonObject.get("day").getAsInt(), jsonObject.get("day").getAsInt(),
                                            jsonObject.get("day").getAsInt(), jsonObject.get("day").getAsInt());
                                    Temperatures.setDimensionDayTemperatures(dimensionIdentifier, jsonObject.get("night").getAsInt(), jsonObject.get("night").getAsInt(),
                                            jsonObject.get("night").getAsInt(), jsonObject.get("night").getAsInt(), jsonObject.get("night").getAsInt());
                                } else {
                                    Temperatures.setDimensionDayTemperatures(dimensionIdentifier, 0, 0, 0, 0, 0);
                                    Temperatures.setDimensionNightTemperatures(dimensionIdentifier, 0, 0, 0, 0, 0);
                                }
                                Temperatures.setDimensionArmorTemperatures(dimensionIdentifier, jsonObject.get("armor").getAsFloat(), jsonObject.get("armor").getAsFloat(),
                                        jsonObject.get("armor").getAsFloat(), jsonObject.get("armor").getAsFloat(), jsonObject.get("armor").getAsFloat());

                                Temperatures.setDimensionInsulatedArmorTemperatures(dimensionIdentifier, jsonObject.get("insulated_armor").getAsFloat(),
                                        jsonObject.get("insulated_armor").getAsFloat(), jsonObject.get("insulated_armor").getAsFloat(), jsonObject.get("insulated_armor").getAsFloat(),
                                        jsonObject.get("insulated_armor").getAsFloat());

                                Temperatures.setDimensionIcedArmorTemperatures(dimensionIdentifier, jsonObject.get("iced_armor").getAsFloat(), jsonObject.get("iced_armor").getAsFloat(),
                                        jsonObject.get("iced_armor").getAsFloat(), jsonObject.get("iced_armor").getAsFloat(), jsonObject.get("iced_armor").getAsFloat());

                                Temperatures.setDimensionSoakedTemperatures(dimensionIdentifier, jsonObject.get("soaked").getAsInt(), jsonObject.get("soaked").getAsInt(),
                                        jsonObject.get("soaked").getAsInt(), jsonObject.get("soaked").getAsInt(), jsonObject.get("soaked").getAsInt());

                                Temperatures.setDimensionWettTemperatures(dimensionIdentifier, jsonObject.get("wett").getAsInt(), jsonObject.get("wett").getAsInt(), jsonObject.get("wett").getAsInt(),
                                        jsonObject.get("wett").getAsInt(), jsonObject.get("wett").getAsInt());

                                Temperatures.setDimensionSweatTemperatures(dimensionIdentifier, jsonObject.get("sweat").getAsInt(), jsonObject.get("sweat").getAsInt());

                                Temperatures.setDimensionShadowTemperatures(dimensionIdentifier, jsonObject.get("shadow").getAsInt(), jsonObject.get("shadow").getAsInt(),
                                        jsonObject.get("shadow").getAsInt(), jsonObject.get("shadow").getAsInt(), jsonObject.get("shadow").getAsInt());

                                Temperatures.setDimensionHeightTemperatures(dimensionIdentifier, jsonObject.get("height").getAsInt(), jsonObject.get("height").getAsInt(),
                                        jsonObject.get("height").getAsInt(), jsonObject.get("height").getAsInt(), jsonObject.get("height").getAsInt(), jsonObject.get("height").getAsInt(),
                                        jsonObject.get("height").getAsInt(), jsonObject.get("height").getAsInt());

                                if (jsonObject.has("acclimatization")) {
                                    Temperatures.setDimensionAcclimatization(dimensionIdentifier, jsonObject.get("acclimatization").getAsInt());
                                }
                            } else {
                                if (jsonObject.has("standard")) {
                                    Temperatures.setDimensionStandardTemperatures(dimensionIdentifier, jsonObject.get("standard").getAsJsonObject().get("very_cold").getAsInt(),
                                            jsonObject.get("standard").getAsJsonObject().get("cold").getAsInt(), jsonObject.get("standard").getAsJsonObject().get("normal").getAsInt(),
                                            jsonObject.get("standard").getAsJsonObject().get("hot").getAsInt(), jsonObject.get("standard").getAsJsonObject().get("very_hot").getAsInt());
                                } else {
                                    Temperatures.setDimensionStandardTemperatures(dimensionIdentifier, 0, 0, 0, 0, 0);
                                }
                                if (jsonObject.has("day") && jsonObject.has("night")) {
                                    Temperatures.setDimensionDayTemperatures(dimensionIdentifier, jsonObject.get("day").getAsJsonObject().get("very_cold").getAsInt(),
                                            jsonObject.get("day").getAsJsonObject().get("cold").getAsInt(), jsonObject.get("day").getAsJsonObject().get("normal").getAsInt(),
                                            jsonObject.get("day").getAsJsonObject().get("hot").getAsInt(), jsonObject.get("day").getAsJsonObject().get("very_hot").getAsInt());
                                    Temperatures.setDimensionNightTemperatures(dimensionIdentifier, jsonObject.get("night").getAsJsonObject().get("very_cold").getAsInt(),
                                            jsonObject.get("night").getAsJsonObject().get("cold").getAsInt(), jsonObject.get("night").getAsJsonObject().get("normal").getAsInt(),
                                            jsonObject.get("night").getAsJsonObject().get("hot").getAsInt(), jsonObject.get("night").getAsJsonObject().get("very_hot").getAsInt());
                                } else {
                                    Temperatures.setDimensionDayTemperatures(dimensionIdentifier, 0, 0, 0, 0, 0);
                                    Temperatures.setDimensionNightTemperatures(dimensionIdentifier, 0, 0, 0, 0, 0);
                                }
                                Temperatures.setDimensionArmorTemperatures(dimensionIdentifier, jsonObject.get("armor").getAsJsonObject().get("very_cold").getAsFloat(),
                                        jsonObject.get("armor").getAsJsonObject().get("cold").getAsFloat(), jsonObject.get("armor").getAsJsonObject().get("normal").getAsFloat(),
                                        jsonObject.get("armor").getAsJsonObject().get("hot").getAsFloat(), jsonObject.get("armor").getAsJsonObject().get("very_hot").getAsFloat());

                                Temperatures.setDimensionInsulatedArmorTemperatures(dimensionIdentifier, jsonObject.get("insulated_armor").getAsJsonObject().get("very_cold").getAsFloat(),
                                        jsonObject.get("insulated_armor").getAsJsonObject().get("cold").getAsFloat(), jsonObject.get("insulated_armor").getAsJsonObject().get("normal").getAsFloat(),
                                        jsonObject.get("insulated_armor").getAsJsonObject().get("hot").getAsFloat(), jsonObject.get("insulated_armor").getAsJsonObject().get("very_hot").getAsFloat());

                                Temperatures.setDimensionIcedArmorTemperatures(dimensionIdentifier, jsonObject.get("iced_armor").getAsJsonObject().get("very_cold").getAsFloat(),
                                        jsonObject.get("iced_armor").getAsJsonObject().get("cold").getAsFloat(), jsonObject.get("iced_armor").getAsJsonObject().get("normal").getAsFloat(),
                                        jsonObject.get("iced_armor").getAsJsonObject().get("hot").getAsFloat(), jsonObject.get("iced_armor").getAsJsonObject().get("very_hot").getAsFloat());

                                Temperatures.setDimensionSoakedTemperatures(dimensionIdentifier, jsonObject.get("soaked").getAsJsonObject().get("very_cold").getAsInt(),
                                        jsonObject.get("soaked").getAsJsonObject().get("cold").getAsInt(), jsonObject.get("soaked").getAsJsonObject().get("normal").getAsInt(),
                                        jsonObject.get("soaked").getAsJsonObject().get("hot").getAsInt(), jsonObject.get("soaked").getAsJsonObject().get("very_hot").getAsInt());

                                Temperatures.setDimensionWettTemperatures(dimensionIdentifier, jsonObject.get("wett").getAsJsonObject().get("very_cold").getAsInt(),
                                        jsonObject.get("wett").getAsJsonObject().get("cold").getAsInt(), jsonObject.get("wett").getAsJsonObject().get("normal").getAsInt(),
                                        jsonObject.get("wett").getAsJsonObject().get("hot").getAsInt(), jsonObject.get("wett").getAsJsonObject().get("very_hot").getAsInt());

                                Temperatures.setDimensionSweatTemperatures(dimensionIdentifier, jsonObject.get("sweat").getAsJsonObject().get("hot").getAsInt(),
                                        jsonObject.get("sweat").getAsJsonObject().get("very_hot").getAsInt());

                                Temperatures.setDimensionShadowTemperatures(dimensionIdentifier, jsonObject.get("shadow").getAsJsonObject().get("very_cold").getAsInt(),
                                        jsonObject.get("shadow").getAsJsonObject().get("cold").getAsInt(), jsonObject.get("shadow").getAsJsonObject().get("normal").getAsInt(),
                                        jsonObject.get("shadow").getAsJsonObject().get("hot").getAsInt(), jsonObject.get("shadow").getAsJsonObject().get("very_hot").getAsInt());

                                Temperatures.setDimensionHeightTemperatures(dimensionIdentifier, jsonObject.get("height").getAsJsonObject().get("very_low").getAsInt(),
                                        jsonObject.get("height").getAsJsonObject().get("low").getAsInt(), jsonObject.get("height").getAsJsonObject().get("high").getAsInt(),
                                        jsonObject.get("height").getAsJsonObject().get("very_high").getAsInt(), jsonObject.get("height").getAsJsonObject().get("very_low_height").getAsInt(),
                                        jsonObject.get("height").getAsJsonObject().get("low_height").getAsInt(), jsonObject.get("height").getAsJsonObject().get("high_height").getAsInt(),
                                        jsonObject.get("height").getAsJsonObject().get("very_high_height").getAsInt());

                            }
                            // System.out.println("Standard: " + keyString + " : " + Temperatures.getDimensionStandardTemperatures(dimensionIdentifier, 3));
                        }
                    }
                }

            } catch (Exception e) {
                EnvironmentzMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}
