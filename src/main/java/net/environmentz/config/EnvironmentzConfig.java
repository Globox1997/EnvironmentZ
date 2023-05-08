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
    @Comment("Try find an easy world spawn point")
    public boolean easy_world_spawn = true;
    @ConfigEntry.Category("general_settings")
    public int icon_x = 7;
    @ConfigEntry.Category("general_settings")
    public int icon_y = 52;
    @ConfigEntry.Category("general_settings")
    public int thermometer_icon_x = 140;
    @ConfigEntry.Category("general_settings")
    public int thermometer_icon_y = 32;
    @ConfigEntry.Category("general_settings")
    public boolean show_thermometer = true;
    @ConfigEntry.Category("general_settings")
    @Comment("Handle with care!")
    public int heat_block_radius = 3;
    @ConfigEntry.Category("general_settings")
    public boolean printInConsole = false;

    // Freezing Settings
    @ConfigEntry.Category("freezing_settings")
    @Comment("White overlay while in cold biomes")
    public boolean cold_overlay = true;
    @ConfigEntry.Category("freezing_settings")
    public boolean shaking_screen_effect = true;

    // Overheating Settings
    @ConfigEntry.Category("overheating_settings")
    @Comment("Handles thirst when Dehydration mod is loaded")
    public float overheating_exhaustion = 0.5F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Undo dehydration mod compat")
    public boolean exhaustion_instead_dehydration = false;
    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("overheating_settings")
    public boolean blur_screen_effect = true;

}