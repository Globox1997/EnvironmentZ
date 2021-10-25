package net.environmentz;

import net.fabricmc.api.ModInitializer;
import net.environmentz.init.*;
import net.environmentz.network.EnvironmentServerPacket;

public class EnvironmentzMain implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandInit.init();
        ConfigInit.init();
        EffectInit.init();
        ItemInit.init();
        LootInit.init();
        TagInit.init();
        EnvironmentServerPacket.init();
    }
}

// You are LOVED!!!
// Jesus loves you unconditional!