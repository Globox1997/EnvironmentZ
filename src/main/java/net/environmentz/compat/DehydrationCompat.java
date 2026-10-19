package net.environmentz.compat;

import net.dehydration.access.ThirstManagerAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

public final class DehydrationCompat {

    public static final boolean IS_LOADED = FabricLoader.getInstance().isModLoaded("dehydration");

    private DehydrationCompat() {
    }

    public static int getThirstLevel(PlayerEntity player) {
        return ((ThirstManagerAccess) player).getThirstManager().getThirstLevel();
    }

    public static void addDehydration(PlayerEntity player, float amount) {
        ((ThirstManagerAccess) player).getThirstManager().addDehydration(amount);
    }
}
