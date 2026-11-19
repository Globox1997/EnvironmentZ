package net.environmentz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.environmentz.config.EnvironmentzConfig;

public class ConfigInit {
    public static EnvironmentzConfig CONFIG = new EnvironmentzConfig();

    public static void init() {
        AutoConfig.register(EnvironmentzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(EnvironmentzConfig.class).getConfig();
    }

}
