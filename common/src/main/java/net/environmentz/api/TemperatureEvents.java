package net.environmentz.api;

import net.environmentz.temperature.data.EnvironmentCode;
import net.minecraft.server.level.ServerPlayer;

public final class TemperatureEvents {

    public static final Event<ModifyTemperature> MODIFY_TEMPERATURE = Event.create(callbacks -> (player, environmentCode, temperature) -> {
        int result = temperature;
        for (ModifyTemperature callback : callbacks) {
            result = callback.modifyTemperature(player, environmentCode, result);
        }
        return result;
    });

    public static final Event<TemperatureChanged> TEMPERATURE_CHANGED = Event.create(callbacks -> (player, oldTemperature, newTemperature) -> {
        for (TemperatureChanged callback : callbacks) {
            callback.onTemperatureChanged(player, oldTemperature, newTemperature);
        }
    });

    private TemperatureEvents() {
    }

    @FunctionalInterface
    public interface ModifyTemperature {
        int modifyTemperature(ServerPlayer player, EnvironmentCode environmentCode, int temperature);
    }

    @FunctionalInterface
    public interface TemperatureChanged {
        void onTemperatureChanged(ServerPlayer player, int oldTemperature, int newTemperature);
    }
}
