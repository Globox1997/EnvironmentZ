package net.environmentz.compat;

import net.dehydration.access.ThirstManagerAccess;
import net.environmentz.platform.Services;
import net.minecraft.world.entity.player.Player;

public final class DehydrationCompat {

    public static final boolean IS_LOADED = Services.PLATFORM.isModLoaded("dehydration");

    private DehydrationCompat() {
    }

    public static int getThirstLevel(Player player) {
        return ((ThirstManagerAccess) player).getThirstManager().getThirstLevel();
    }

    public static void addDehydration(Player player, float amount) {
        ((ThirstManagerAccess) player).getThirstManager().addDehydration(amount);
    }
}
