package net.environmentz.api;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.environmentz.temperature.data.BodyTemperature;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class EnvironmentzApi {

    private EnvironmentzApi() {
    }

    public static TemperatureManager getTemperatureManager(Player player) {
        return ((TemperatureManagerAccess) player).getTemperatureManager();
    }

    public static int getTemperature(Player player) {
        return getTemperatureManager(player).getPlayerTemperature();
    }

    public static int getWetness(Player player) {
        return getTemperatureManager(player).getPlayerWetIntensityValue();
    }

    public static boolean isHeatAffected(Player player) {
        return getTemperatureManager(player).isHotEnvAffected();
    }

    public static boolean isColdAffected(Player player) {
        return getTemperatureManager(player).isColdEnvAffected();
    }

    public static boolean isFreezing(Player player) {
        return getTemperature(player) <= Temperatures.getBodyTemperature().maxCold();
    }

    public static boolean isOverheating(Player player) {
        return getTemperature(player) >= Temperatures.getBodyTemperature().maxHot();
    }

    public static BodyTemperature getBodyTemperatureThresholds() {
        return Temperatures.getBodyTemperature();
    }

    public static void setTemperature(ServerPlayer player, int temperature) {
        TemperatureManager temperatureManager = getTemperatureManager(player);
        int oldTemperature = temperatureManager.getPlayerTemperature();
        temperatureManager.setPlayerTemperature(temperature);
        EnvironmentServerPacket.syncTemperature(player, false);
        if (oldTemperature != temperature) {
            TemperatureEvents.TEMPERATURE_CHANGED.invoker().onTemperatureChanged(player, oldTemperature, temperature);
        }
    }

    public static void setEnvironmentAffection(ServerPlayer player, boolean heatAffected, boolean coldAffected) {
        getTemperatureManager(player).setEnvironmentAffection(heatAffected, coldAffected);
        EnvironmentServerPacket.writeS2CSyncEnvPacket(player, heatAffected, coldAffected);
    }

    public static void setResistance(ServerPlayer player, int heatResistance, int coldResistance) {
        TemperatureManager temperatureManager = getTemperatureManager(player);
        temperatureManager.setPlayerHeatResistance(heatResistance);
        temperatureManager.setPlayerColdResistance(coldResistance);
    }

    public static void setProtection(ServerPlayer player, int heatProtection, int coldProtection) {
        TemperatureManager temperatureManager = getTemperatureManager(player);
        temperatureManager.setPlayerHeatProtectionAmount(heatProtection);
        temperatureManager.setPlayerColdProtectionAmount(coldProtection);
    }
}
