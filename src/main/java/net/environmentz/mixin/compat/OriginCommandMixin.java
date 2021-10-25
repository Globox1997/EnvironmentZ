package net.environmentz.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.command.OriginCommand;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import me.shedaniel.autoconfig.ConfigData.ValidationException;
import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(OriginCommand.class)
public class OriginCommandMixin {

    @Inject(method = "setOrigin", at = @At("TAIL"), remap = false)
    private static void setOriginMixin(PlayerEntity player, OriginLayer layer, Origin origin, CallbackInfo info) throws ValidationException {
        if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("fire_immunity")))) {
            if (((PlayerEnvAccess) player).isHotEnvAffected()) {
                ((PlayerEnvAccess) player).setHotEnvAffected(false);
            }
        } else if (!((PlayerEnvAccess) player).isHotEnvAffected()) {
            ((PlayerEnvAccess) player).setHotEnvAffected(true);
        }
        if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("freeze")))) {
            if (((PlayerEnvAccess) player).isColdEnvAffected()) {
                ((PlayerEnvAccess) player).setColdEnvAffected(false);
            }
        } else if (!((PlayerEnvAccess) player).isColdEnvAffected()) {
            ((PlayerEnvAccess) player).setColdEnvAffected(true);
        }

        if (player instanceof ServerPlayerEntity) {
            EnvironmentServerPacket.writeS2CSyncEnvPacket((ServerPlayerEntity) player, ((PlayerEnvAccess) player).isHotEnvAffected(), ((PlayerEnvAccess) player).isColdEnvAffected());
            ((PlayerEnvAccess) player).compatSync();
        }
    }
}
