package net.environmentz.init;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.environmentz.config.EnvironmentzConfig;

public class ConfigInit {
  public static EnvironmentzConfig CONFIG = new EnvironmentzConfig();

  public static void init() {
    AutoConfig.register(EnvironmentzConfig.class, JanksonConfigSerializer::new);
    CONFIG = AutoConfig.getConfigHolder(EnvironmentzConfig.class).getConfig();
  }

}
