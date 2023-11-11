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
    public int temperatureCalculationTime = 20;
    @ConfigEntry.Category("general_settings")
    @Comment("Try find an easy world spawn point")
    public boolean easyWorldSpawn = true;
    @ConfigEntry.Category("general_settings")
    public int iconX = 7;
    @ConfigEntry.Category("general_settings")
    public int iconY = 52;
    @ConfigEntry.Category("general_settings")
    public int thermometerIconX = 140;
    @ConfigEntry.Category("general_settings")
    public int thermometerIconY = 32;
    @ConfigEntry.Category("general_settings")
    public boolean showThermometer = true;
    @ConfigEntry.Category("general_settings")
    public int startUpComfortEffectDuration = 9600;
    @ConfigEntry.Category("general_settings")
    @Comment("Handle with care!")
    public int heatBlockRadius = 3;
    @ConfigEntry.Category("general_settings")
    public boolean printInConsole = false;

    // Freezing Settings
    @ConfigEntry.Category("freezing_settings")
    @Comment("White overlay while in cold biomes")
    public boolean coldOverlay = true;
    @ConfigEntry.Category("freezing_settings")
    public boolean shakingScreenEffect = true;

    // Overheating Settings
    @ConfigEntry.Category("overheating_settings")
    @Comment("Handles thirst when Dehydration mod is loaded")
    public float overheatingExhaustion = 0.075F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Undo dehydration mod compat")
    public boolean exhaustionInsteadDehydration = false;
    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("overheating_settings")
    public boolean blurScreenEffect = true;

}