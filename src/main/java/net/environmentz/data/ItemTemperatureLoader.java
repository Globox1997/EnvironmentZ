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

public class ItemTemperatureLoader implements SimpleSynchronousResourceReloadListener {

    // List to store replacing bools
    private ArrayList<String> replaceList = new ArrayList<String>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("environmentz", "item_temperature_loader");
    }

    @Override
    public void reload(ResourceManager manager) {

        manager.findResources("environment_items", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                Iterator<String> iterator = data.keySet().iterator();
                while (iterator.hasNext()) {

                    String keyString = iterator.next();

                    if (replaceList.contains(keyString)) {
                        continue;
                    }
                    Identifier identifier = new Identifier(keyString);
                    if (Registry.ITEM.get(identifier).toString().equals("air")) {
                        EnvironmentzMain.LOGGER.info("{} is not a valid item identifier", identifier);
                        continue;
                    }

                    JsonElement jsonElement = data.get(keyString);

                    if (jsonElement != null && jsonElement instanceof JsonObject) {
                        JsonObject jsonObject = (JsonObject) jsonElement;

                        if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                            replaceList.add(keyString);
                        }

                        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();

                        hashMap.put(0, jsonObject.get("temperature").getAsInt());
                        if (jsonObject.has("damage")) {
                            hashMap.put(-1, jsonObject.get("damage").getAsInt());
                        } else {
                            hashMap.put(-1, 0);
                        }
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
                        int rawId = Registry.ITEM.getRawId(Registry.ITEM.get(identifier));
                        Temperatures.setItemTemperatures(rawId, hashMap);
                    }
                }
            } catch (Exception e) {
                EnvironmentzMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}
