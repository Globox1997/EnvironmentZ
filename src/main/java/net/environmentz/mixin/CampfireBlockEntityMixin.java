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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(value = CampfireBlockEntity.class, priority = 1001)
public class CampfireBlockEntityMixin {

    @Unique
    private HashMap<UUID, Integer> playerComfortMap = new HashMap<>();

    @Inject(method = "litServerTick", at = @At("TAIL"))
    private static void litServerTickMixin(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo info) {
        if (world.getTime() % 200 == 0) {
            List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, new Box(pos.up()).expand(4.0D, 2.0D, 4.0D), EntityPredicates.EXCEPT_SPECTATOR);
            if (!playerEntities.isEmpty()) {
                List<UUID> playerUuids = new ArrayList<>();
                HashMap<UUID, Integer> currentPlayerComfortMap = ((CampfireBlockEntityMixin) (Object) campfire).playerComfortMap;

                for (PlayerEntity playerEntity : playerEntities) {
                    UUID uuid = playerEntity.getUuid();
                    if (currentPlayerComfortMap.containsKey(uuid)) {
                        currentPlayerComfortMap.put(uuid, currentPlayerComfortMap.get(uuid) + 1);
                        if (currentPlayerComfortMap.get(uuid) > 6) {
                            playerEntity.addStatusEffect(new StatusEffectInstance(EffectInit.COMFORT, 2400, 0, false, false, true));
                            currentPlayerComfortMap.put(uuid, 0);
                        }
                    } else {
                        currentPlayerComfortMap.put(uuid, 1);
                    }
                    playerUuids.add(uuid);
                }
                playerUuids.add(UUID.randomUUID());
                playerUuids.add(UUID.randomUUID());

                currentPlayerComfortMap.entrySet().removeIf(entry -> !playerUuids.contains(entry.getKey()));
            } else {
                ((CampfireBlockEntityMixin) (Object) campfire).playerComfortMap.clear();
            }
        }

    }
}
