package net.environmentz.init;

import java.util.List;
import java.util.function.BiConsumer;

import net.environmentz.data.BlockTemperatureLoader;
import net.environmentz.data.ItemTemperatureLoader;
import net.environmentz.data.TemperatureDataLoader;
import net.environmentz.data.TemperatureManagerLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class LoaderInit {

    public static void register(BiConsumer<Identifier, PreparableReloadListener> registrar) {
        for (TemperatureDataLoader loader : List.of(new BlockTemperatureLoader(), new ItemTemperatureLoader(), new TemperatureManagerLoader())) {
            registrar.accept(loader.getId(), loader);
        }
    }
}
