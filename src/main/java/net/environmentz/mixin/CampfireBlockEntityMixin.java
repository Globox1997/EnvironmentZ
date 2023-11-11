package net.environmentz.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.init.EffectInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {

    private HashMap<UUID, Integer> playerComfortMap = new HashMap<UUID, Integer>();

    @Inject(method = "litServerTick", at = @At("TAIL"))
    private static void litServerTickMixin(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo info) {
        if (world.getTime() % 200 == 0) {
            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, new Box(pos.up()).expand(4.0D, 2.0D, 4.0D), EntityPredicates.EXCEPT_SPECTATOR);
            if (!playerEntities.isEmpty()) {
                List<UUID> playerUuids = new ArrayList<UUID>();
                HashMap<UUID, Integer> currentPlayerComfortMap = ((CampfireBlockEntityMixin) (Object) campfire).playerComfortMap;

                for (int i = 0; i < playerEntities.size(); i++) {
                    UUID uuid = playerEntities.get(i).getUuid();
                    if (currentPlayerComfortMap.containsKey(uuid)) {
                        currentPlayerComfortMap.put(uuid, currentPlayerComfortMap.get(uuid) + 1);
                        if (currentPlayerComfortMap.get(uuid) > 6) {
                            playerEntities.get(i).addStatusEffect(new StatusEffectInstance(EffectInit.COMFORT, 2400, 0, false, false, true));
                            currentPlayerComfortMap.put(uuid, 0);
                        }
                    } else {
                        currentPlayerComfortMap.put(uuid, 1);
                    }
                    playerUuids.add(uuid);
                }
                playerUuids.add(UUID.randomUUID());
                playerUuids.add(UUID.randomUUID());
                Iterator<Map.Entry<UUID, Integer>> iterator = currentPlayerComfortMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, Integer> entry = iterator.next();
                    if (!playerUuids.contains(entry.getKey())) {
                        iterator.remove();
                    }
                }
            } else {
                ((CampfireBlockEntityMixin) (Object) campfire).playerComfortMap.clear();
            }
        }

    }
}
