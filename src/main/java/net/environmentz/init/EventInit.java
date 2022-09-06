package net.environmentz.init;

import net.environmentz.access.PlayerEnvAccess;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

public class EventInit {

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((PlayerEnvAccess) player).compatSync();
        });
    }
}
