package net.environmentz.init;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;

public class EventInit {

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((PlayerEnvAccess) player).compatSync();
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
            builder.addMix(Potions.AWKWARD, Items.FIRE_CHARGE, ItemInit.COLD_RESISTANCE);
            builder.addMix(ItemInit.COLD_RESISTANCE, Items.REDSTONE, ItemInit.LONG_COLD_RESISTANCE);
            builder.addMix(Potions.AWKWARD, Items.SNOWBALL, ItemInit.OVERHEATING_RESISTANCE);
            builder.addMix(ItemInit.OVERHEATING_RESISTANCE, Items.REDSTONE, ItemInit.LONG_OVERHEATING_RESISTANCE);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EnvironmentServerPacket.writeS2CSyncValuesPacket(handler.player);
            EnvironmentServerPacket.syncAll(handler.player);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            EnvironmentServerPacket.syncAll(newPlayer);
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    EnvironmentServerPacket.writeS2CSyncValuesPacket(player);
                }
            }
        });
    }
}
