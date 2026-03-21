package net.environmentz;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.environmentz.init.*;
import net.environmentz.network.EnvironmentServerPacket;

public class EnvironmentzMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("EnvironmentZ");

    @Override
    public void onInitialize() {
        BlockInit.init();
        CommandInit.init();
        ConfigInit.init();
        EffectInit.init();
        EventInit.init();
        ItemInit.init();
        LoaderInit.init();
        LootInit.init();
        TagInit.init();
        EnvironmentServerPacket.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of("environmentz", name);
    }
}

// You are LOVED!!!
// Jesus loves you unconditional!