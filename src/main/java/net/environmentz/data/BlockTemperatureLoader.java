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
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BlockTemperatureLoader implements SimpleSynchronousResourceReloadListener {

    // List to store replacing bools
    private ArrayList<String> replaceList = new ArrayList<String>();

    @Override
    public Identifier getFabricId() {
        return new Identifier("environmentz", "block_temperature_loader");
    }

    @Override
    public void reload(ResourceManager manager) {

        manager.findResources("environment_blocks", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
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
                    if (Registries.BLOCK.get(identifier).toString().equals("Block{minecraft:air}") && Registries.FLUID.get(identifier).toString().contains("net.minecraft.fluid.EmptyFluid")) {
                        EnvironmentzMain.LOGGER.info("{} is not a valid block or fluid identifier", identifier);
                        continue;
                    }

                    JsonElement jsonElement = data.get(keyString);

                    if (jsonElement != null && jsonElement instanceof JsonObject) {
                        JsonObject jsonObject = (JsonObject) jsonElement;

                        if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                            replaceList.add(keyString);
                        }

                        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
                        hashMap.put(-1, jsonObject.get("max_count").getAsInt());
                        for (int i = 0; i <= ConfigInit.CONFIG.heatBlockRadius; i++) {
                            if (jsonObject.has(String.valueOf(i))) {
                                hashMap.put(i, jsonObject.get(String.valueOf(i)).getAsInt());
                            }
                        }
                        if (Registries.BLOCK.get(identifier).toString().equals("Block{minecraft:air}")) {
                            Temperatures.setFluidTemperatures(Registries.FLUID.getRawId(Registries.FLUID.get(identifier)), hashMap);
                        } else {
                            int rawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(identifier));
                            Temperatures.setBlockTemperatures(rawId, hashMap);
                            if (jsonObject.has("property")) {
                                Temperatures.setBlockProperty(rawId, jsonObject.get("property").getAsString());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                EnvironmentzMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}
