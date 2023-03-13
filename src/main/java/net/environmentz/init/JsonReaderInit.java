package net.environmentz.init;

import net.environmentz.data.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class JsonReaderInit {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new BlockTemperatureLoader());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ItemTemperatureLoader());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TemperatureManagerLoader());
    }

}
