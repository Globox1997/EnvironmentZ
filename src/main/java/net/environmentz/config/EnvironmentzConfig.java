package net.environmentz.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "environmentz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class EnvironmentzConfig implements ConfigData {

  public boolean allow_all_armor = false;
  @ConfigEntry.Gui.PrefixText
  public int cold_damage_interval = 400;
  public float cold_damage = 1.0F;
  public int cold_damage_effect_time = 2400;
  public int cold_tick_interval = 400;
  public int heating_up_interval = 100;
  public int heating_up_cold_tick_decrease = 400;
}