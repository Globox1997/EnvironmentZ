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
    public int icon_x = 7;
    @ConfigEntry.Category("general_settings")
    public int icon_y = 52;

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
    @Comment("Disables white overlay")
    public boolean cold_overlay = true;

    // Overheating Settings
    @ConfigEntry.Category("overheating_settings")
    @Comment("Above this biome temp - start overheating")
    public float biome_hot_temp = 1.2F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Above this biome temp - start overheating")
    public float biome_overheat_temp = 1.6F;
    @ConfigEntry.Category("overheating_settings")
    @Comment("Handles thirst when Dehydration mod is loaded")
    public float overheating_exhaustion = 0.2F;

}