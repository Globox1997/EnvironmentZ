package net.environmentz.api;

import net.environmentz.temperature.data.EnvironmentCode;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class TemperatureEvents {

    public static final Event<ModifyTemperature> MODIFY_TEMPERATURE = EventFactory.createArrayBacked(ModifyTemperature.class, callbacks -> (player, environmentCode, temperature) -> {
        int result = temperature;
        for (ModifyTemperature callback : callbacks) {
            result = callback.modifyTemperature(player, environmentCode, result);
        }
        return result;
    });

    public static final Event<TemperatureChanged> TEMPERATURE_CHANGED = EventFactory.createArrayBacked(TemperatureChanged.class, callbacks -> (player, oldTemperature, newTemperature) -> {
        for (TemperatureChanged callback : callbacks) {
            callback.onTemperatureChanged(player, oldTemperature, newTemperature);
        }
    });

    private TemperatureEvents() {
    }

    @FunctionalInterface
    public interface ModifyTemperature {
        int modifyTemperature(ServerPlayerEntity player, EnvironmentCode environmentCode, int temperature);
    }

    @FunctionalInterface
    public interface TemperatureChanged {
        void onTemperatureChanged(ServerPlayerEntity player, int oldTemperature, int newTemperature);
    }
}
