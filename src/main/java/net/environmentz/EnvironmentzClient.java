package net.environmentz;

import net.environmentz.init.ModelProviderInit;
import net.environmentz.init.RenderInit;
import net.environmentz.network.EnvironmentClientPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnvironmentzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RenderInit.init();
        EnvironmentClientPacket.init();
        ModelProviderInit.init();
    }

}
