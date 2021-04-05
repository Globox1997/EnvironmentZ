package net.environmentz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "environmentz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class EnvironmentzConfig implements ConfigData {

  public boolean allow_all_armor = false;
  public float cold_damage = 1.0F;

  @ConfigEntry.Gui.PrefixText
  @Comment("How often/fast you get the freezing effect in seconds")
  public int cold_tick_interval = 30;
  @Comment("How long the freezing effects last in ticks")
  public int cold_damage_effect_time = 2400;
  public int cold_tick_snowing_bonus = 600;
  @Comment("How often you take freeze damage in ticks")
  public int cold_damage_interval = 400;
  @Comment("Increases the time for freezing in seconds")
  public int warm_armor_tick_modifier = 10;
  @Comment("Time for heating up in seconds")
  public int heating_up_interval = 4;
  @Comment("Substracting freeze effect ticks while heating")
  public int heating_up_cold_tick_decrease = 400;
  @Comment("Caution! This could decrease performance")
  public int heating_up_block_range = 2;
  @Comment("Below max biome temp - start freezing")
  public float max_biome_freeze_temp = 0.2F;
  public float heating_up_biome_temp = 1.0F;
  @ConfigEntry.Gui.PrefixText
  public int freeze_icon_x = 7;
  public int freeze_icon_y = 52;
}