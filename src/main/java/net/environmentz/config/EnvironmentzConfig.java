package net.environmentz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "environmentz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class EnvironmentzConfig implements ConfigData {

    // General Settings
    @ConfigEntry.Category("general_settings")
    @Comment("Increase for less harsh environments")
    public int temperature_calculation_time = 20;
    @ConfigEntry.Category("general_settings")
    public int icon_x = 7;
    @ConfigEntry.Category("general_settings")
    public int icon_y = 52;
    @ConfigEntry.Category("general_settings")
    public int thermometer_icon_x = 255;
    @ConfigEntry.Category("general_settings")
    public int thermometer_icon_y = 32;
    @ConfigEntry.Category("general_settings")
    public boolean show_thermometer = true;

    // Freezing Settings
    @ConfigEntry.Category("freezing_settings")
    @Comment("Below this biome temp - start undercooling")
    public float biome_cold_temp = 0.4F;
    @ConfigEntry.Category("freezing_settings")
    @Comment("Below this biome temp - start freezing")
    public float biome_freeze_temp = 0.2F;
    @ConfigEntry.Category("freezing_settings")
    public boolean allow_all_armor = false;
    @ConfigEntry.Category("freezing_settings")
    @Comment("Caution! This could decrease performance")
    public int heating_up_block_range = 3;
    @ConfigEntry.Category("freezing_settings")
    @Comment("Protection amount is reduced every second")
    public int max_cold_protection_amount = 120;
    @ConfigEntry.Category("freezing_settings")
    @Comment("Protection amount is added every second")
    public int cold_protection_amount_addition = 2;
    @ConfigEntry.Category("freezing_settings")
    @Comment("White overlay while in cold biomes")
    public boolean cold_overlay = true;
    @ConfigEntry.Category("freezing_settings")
    public boolean shaking_screen_effect = true;

    // Overheating Settings
    @ConfigEntry.Category("overheating_settings")
    @Comment("Above this biome temp - start overheating")
    public float biome_hot_temp = 1.2F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Above this biome temp - start overheating")
    public float biome_overheat_temp = 1.6F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Handles thirst when Dehydration mod is loaded")
    public float overheating_exhaustion = 0.5F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Protection amount is reduced every second")
    public int max_heat_protection_amount = 120;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Protection amount is added every second")
    public int heat_protection_amount_addition = 2;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Undo dehydration mod compat")
    public boolean exhaustion_instead_dehydration = false;
    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("overheating_settings")
    public boolean blur_screen_effect = true;

}