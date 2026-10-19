package net.environmentz.data;

import com.google.gson.JsonElement;
import net.environmentz.EnvironmentzMain;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.data.ItemTemperature;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ItemTemperatureLoader extends TemperatureDataLoader {

    @Override
    public Identifier getFabricId() {
        return EnvironmentzMain.identifierOf("item_temperature_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Item, ItemTemperature> items = new HashMap<>();
        Set<String> replaced = new HashSet<>();

        forEachJsonFile(manager, "environment_items", (file, data) -> {
            for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                String key = entry.getKey();
                if (replaced.contains(key)) {
                    continue;
                }
                Identifier identifier = Identifier.tryParse(key);
                Optional<Item> item = identifier == null ? Optional.empty() : Registries.ITEM.getOrEmpty(identifier);
                if (item.isEmpty()) {
                    EnvironmentzMain.LOGGER.info("{} is not a valid item identifier", key);
                    continue;
                }
                if (isReplacing(entry.getValue())) {
                    replaced.add(key);
                }
                parse(ItemTemperature.CODEC, entry.getValue(), file, key).ifPresent(itemTemperature -> items.put(item.get(), itemTemperature));
            }
        });

        Temperatures.setItems(items);
    }
}
