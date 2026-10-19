package net.environmentz.init;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventInit {

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            ((PlayerEnvAccess) player).compatSync();
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register((builder) -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Items.FIRE_CHARGE, ItemInit.COLD_RESISTANCE);
            builder.registerPotionRecipe(ItemInit.COLD_RESISTANCE, Items.REDSTONE, ItemInit.LONG_COLD_RESISTANCE);
            builder.registerPotionRecipe(Potions.AWKWARD, Items.SNOWBALL, ItemInit.OVERHEATING_RESISTANCE);
            builder.registerPotionRecipe(ItemInit.OVERHEATING_RESISTANCE, Items.REDSTONE, ItemInit.LONG_OVERHEATING_RESISTANCE);
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
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    EnvironmentServerPacket.writeS2CSyncValuesPacket(player);
                }
            }
        });
    }
}
