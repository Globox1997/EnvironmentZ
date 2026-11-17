package net.environmentz.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.EffectInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

@Mixin(value = CampfireBlockEntity.class, priority = 1001)
public class CampfireBlockEntityMixin {

    @Unique
    private HashMap<UUID, Integer> playerComfortMap = new HashMap<>();

    @Inject(method = "cookTick", at = @At("TAIL"))
    private static void litServerTickMixin(Level world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo info) {
        if (world.getGameTime() % 200 == 0) {
            List<Player> playerEntities = world.getEntitiesOfClass(Player.class, new AABB(pos.above()).inflate(4.0D, 2.0D, 4.0D), EntitySelector.NO_SPECTATORS);
            if (!playerEntities.isEmpty()) {
                List<UUID> playerUuids = new ArrayList<>();
                HashMap<UUID, Integer> currentPlayerComfortMap = ((CampfireBlockEntityMixin) (Object) campfire).playerComfortMap;

                for (Player playerEntity : playerEntities) {
                    UUID uuid = playerEntity.getUUID();
                    if (currentPlayerComfortMap.containsKey(uuid)) {
                        currentPlayerComfortMap.put(uuid, currentPlayerComfortMap.get(uuid) + 1);
                        if (currentPlayerComfortMap.get(uuid) > 6) {
                            playerEntity.addEffect(new MobEffectInstance(EffectInit.COMFORT, 2400, 0, false, false, true));
                            currentPlayerComfortMap.put(uuid, 0);
                        }
                    } else {
                        currentPlayerComfortMap.put(uuid, 1);
                    }
                    playerUuids.add(uuid);
                }

                currentPlayerComfortMap.entrySet().removeIf(entry -> !playerUuids.contains(entry.getKey()));
            } else {
                ((CampfireBlockEntityMixin) (Object) campfire).playerComfortMap.clear();
            }
        }

    }
}
