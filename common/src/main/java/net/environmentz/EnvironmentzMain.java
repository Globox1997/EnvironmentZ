package net.environmentz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.Identifier;
import net.environmentz.init.*;

public class EnvironmentzMain {

    public static final String MOD_ID = "environmentz";

    public static final Logger LOGGER = LogManager.getLogger("EnvironmentZ");

    public static void init() {
        ConfigInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }
}

// You are LOVED!!!
// Jesus loves you unconditional!
