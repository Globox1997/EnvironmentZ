package net.environmentz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.environmentz.EnvironmentzMain;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.data.BlockTemperature;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.*;

public class BlockTemperatureLoader extends TemperatureDataLoader {

    @Override
    public Identifier getFabricId() {
        return EnvironmentzMain.identifierOf("block_temperature_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Block, BlockTemperature> blocks = new HashMap<>();
        Map<Fluid, BlockTemperature> fluids = new HashMap<>();
        Set<String> replaced = new HashSet<>();

        forEachJsonFile(manager, "environment_blocks", (file, data) -> {
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                if (replaced.contains(key)) {
                    continue;
                }
                Identifier identifier = Identifier.tryParse(key);
                Optional<Block> block = identifier == null ? Optional.empty() : Registries.BLOCK.getOrEmpty(identifier);
                Optional<Fluid> fluid = identifier == null || block.isPresent() ? Optional.empty() : Registries.FLUID.getOrEmpty(identifier);
                if (block.isEmpty() && fluid.isEmpty()) {
                    EnvironmentzMain.LOGGER.info("{} is not a valid block or fluid identifier", key);
                    continue;
                }
                if (!(entry.getValue() instanceof JsonObject jsonObject)) {
                    continue;
                }
                if (isReplacing(jsonObject)) {
                    replaced.add(key);
                }
                try {
                    BlockTemperature blockTemperature = parseBlockTemperature(jsonObject);
                    block.ifPresent(value -> blocks.put(value, blockTemperature));
                    fluid.ifPresent(value -> fluids.put(value, blockTemperature));
                } catch (RuntimeException exception) {
                    EnvironmentzMain.LOGGER.error("Invalid entry {} in {}: {}", key, file, exception.getMessage());
                }
            }
        });

        Temperatures.setBlocks(blocks, fluids);
    }

    private static BlockTemperature parseBlockTemperature(JsonObject jsonObject) {
        int maxCount = JsonHelper.getInt(jsonObject, "max_count");
        Map<Integer, Integer> distances = new HashMap<>();
        int maxDistance = -1;
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            Integer distance = parseDistance(entry.getKey());
            if (distance != null) {
                distances.put(distance, JsonHelper.asInt(entry.getValue(), entry.getKey()));
                maxDistance = Math.max(maxDistance, distance);
            }
        }
        int[] temperatures = new int[maxDistance + 1];
        distances.forEach((distance, temperature) -> temperatures[distance] = temperature);

        BooleanProperty property = jsonObject.has("property") ? BooleanProperty.of(JsonHelper.getString(jsonObject, "property")) : null;
        return new BlockTemperature(maxCount, temperatures, property);
    }

    private static Integer parseDistance(String key) {
        try {
            int distance = Integer.parseInt(key);
            return distance >= 0 ? distance : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
