package net.environmentz.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "environmentz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class EnvironmentzConfig implements ConfigData {

  public boolean allow_all_armor = false;
  public float cold_damage = 1.0F;
  @ConfigEntry.Gui.PrefixText
  public int cold_damage_interval = 400;
  @Comment("Freeze effect duration ticks")
  public int cold_damage_effect_time = 2400;
  @Comment("Time after freezing occurs")
  public int cold_tick_interval = 400;
  public int warm_armor_tick_modifier = 40;
  public int heating_up_interval = 100;
  @Comment("Substracting freeze effect ticks")
  public int heating_up_cold_tick_decrease = 400;
  @Comment("Caution! This could decrease performance")
  public int heating_up_block_range = 1;
  @ConfigEntry.Gui.PrefixText
  public int freeze_icon_x = 7;
  public int freeze_icon_y = 52;
}