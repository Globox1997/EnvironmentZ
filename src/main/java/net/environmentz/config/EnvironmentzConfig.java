package net.environmentz.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "environmentz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class EnvironmentzConfig implements ConfigData {

  // General Settings
  @ConfigEntry.Category("general_settings")
  public float acclimatize_biome_temp = 1.0F;
  @ConfigEntry.Category("general_settings")
  public int wet_effect_time = 609;
  @ConfigEntry.Category("general_settings")
  @Comment("Wet modificator - every other second")
  public int wet_bonus_malus = 2;
  @ConfigEntry.Category("general_settings")
  @ConfigEntry.Gui.PrefixText
  public int icon_x = 7;
  @ConfigEntry.Category("general_settings")
  public int icon_y = 52;

  // Freezing Settings
  @ConfigEntry.Category("freezing_settings")
  public boolean allow_all_armor = false;
  @ConfigEntry.Category("freezing_settings")
  public float cold_damage = 1.0F;
  @ConfigEntry.Category("freezing_settings")
  @ConfigEntry.Gui.PrefixText
  @Comment("How often/fast you get the freezing effect in seconds")
  public int cold_tick_interval = 60;
  @ConfigEntry.Category("freezing_settings")
  @Comment("How long the freezing effects last in ticks")
  public int cold_damage_effect_time = 2400;
  @ConfigEntry.Category("freezing_settings")
  public int cold_tick_snowing_bonus = 600;
  @ConfigEntry.Category("freezing_settings")
  @Comment("How often you take freeze damage in ticks")
  public int cold_damage_interval = 400;
  @ConfigEntry.Category("freezing_settings")
  @Comment("Increases the time for freezing in seconds")
  public int warm_armor_tick_modifier = 20;
  @ConfigEntry.Category("freezing_settings")
  @Comment("Time for heating up in seconds")
  public int heating_up_interval = 4;
  @ConfigEntry.Category("freezing_settings")
  @Comment("Substracting freeze effect ticks while heating")
  public int heating_up_cold_tick_decrease = 400;
  @ConfigEntry.Category("freezing_settings")
  @Comment("Caution! This could decrease performance")
  public int heating_up_block_range = 2;
  @ConfigEntry.Category("freezing_settings")
  @Comment("Below this biome temp - start freezing")
  public float biome_freeze_temp = 0.2F;
  @ConfigEntry.Category("freezing_settings")
  @Comment("Players listed here won't have thirst")
  public List<String> excluded_cold_names = new ArrayList<>();

  // Overheating Settings
  @ConfigEntry.Category("overheating_settings")
  public boolean disable_armor_debuff = false;
  @ConfigEntry.Category("overheating_settings")
  @ConfigEntry.Gui.PrefixText
  @Comment("Time after overheating occurs in seconds")
  public int overheating_tick_interval = 40;
  @ConfigEntry.Category("overheating_settings")
  public int overheating_damage_effect_time = 2400;
  @ConfigEntry.Category("overheating_settings")
  public int overheating_damage_interval = 400;
  @ConfigEntry.Category("overheating_settings")
  public int cooling_down_interval = 5;
  @ConfigEntry.Category("overheating_settings")
  public int cooling_down_tick_decrease = 400;
  @ConfigEntry.Category("overheating_settings")
  @Comment("Above this biome temp - start overheating")
  public float biome_overheat_temp = 2.0F;
  @ConfigEntry.Category("overheating_settings")
  @Comment("Players listed here won't overheat")
  public List<String> excluded_heat_names = new ArrayList<>();

  // Dehydration Compatibility Settings
  @ConfigEntry.Category("dehydration_settings")
  @ConfigEntry.Gui.PrefixText
  @Comment("Thirst timer while overheating")
  public int overheating_dehydration_timer = 4;
  @ConfigEntry.Category("dehydration_settings")
  public float overheating_dehydration_thirst = 0.5F;

  // public double test1 = 0.0D;
  // public double test2 = -1.75D;
  // public double test3 = -0.1D;
}