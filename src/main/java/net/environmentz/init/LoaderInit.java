package net.environmentz.init;

import net.environmentz.data.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class LoaderInit {

    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new BlockTemperatureLoader());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ItemTemperatureLoader());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new TemperatureManagerLoader());
    }

}
