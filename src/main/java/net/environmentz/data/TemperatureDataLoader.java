package net.environmentz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.environmentz.EnvironmentzMain;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.Reader;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class TemperatureDataLoader implements SimpleSynchronousResourceReloadListener {

    protected static void forEachJsonFile(ResourceManager manager, String directory, BiConsumer<Identifier, JsonObject> consumer) {
        manager.findResources(directory, id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
            try (Reader reader = resource.getReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                if (json instanceof JsonObject jsonObject) {
                    consumer.accept(id, jsonObject);
                } else {
                    EnvironmentzMain.LOGGER.error("Resource {} is not a json object", id);
                }
            } catch (Exception exception) {
                EnvironmentzMain.LOGGER.error("Error occurred while loading resource {}", id, exception);
            }
        });
    }

    protected static <T> Optional<T> parse(Codec<T> codec, JsonElement json, Identifier file, String key) {
        return codec.parse(JsonOps.INSTANCE, json).resultOrPartial(error -> EnvironmentzMain.LOGGER.error("Invalid entry {} in {}: {}", key, file, error));
    }

    protected static boolean isReplacing(JsonElement json) {
        return json instanceof JsonObject jsonObject && JsonHelper.getBoolean(jsonObject, "replace", false);
    }

    @Override
    public abstract void reload(ResourceManager manager);
}
