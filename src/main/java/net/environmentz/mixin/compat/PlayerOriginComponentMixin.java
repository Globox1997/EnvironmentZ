package net.environmentz.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

// import io.github.apace100.apoli.power.PowerTypeReference;
// import io.github.apace100.origins.Origins;
// import io.github.apace100.origins.component.PlayerOriginComponent;
// import io.github.apace100.origins.origin.Origin;
// import io.github.apace100.origins.origin.OriginLayer;
import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@SuppressWarnings("unused")
// @Mixin(PlayerOriginComponent.class)
public class PlayerOriginComponentMixin {

    // @Shadow
    // private PlayerEntity player;

    // @Inject(method = "setOrigin", at = @At("TAIL"), remap = false)
    // private void setOriginMixin(OriginLayer layer, Origin origin, CallbackInfo info) {
    // if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("fire_immunity")))) {
    // if (((PlayerEnvAccess) player).isHotEnvAffected()) {
    // ((PlayerEnvAccess) player).setHotEnvAffected(false);
    // }
    // } else if (!((PlayerEnvAccess) player).isHotEnvAffected()) {
    // ((PlayerEnvAccess) player).setHotEnvAffected(true);
    // }
    // if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("freeze")))) {
    // if (((PlayerEnvAccess) player).isColdEnvAffected()) {
    // ((PlayerEnvAccess) player).setColdEnvAffected(false);
    // }
    // } else if (!((PlayerEnvAccess) player).isColdEnvAffected()) {
    // ((PlayerEnvAccess) player).setColdEnvAffected(true);
    // }

    // if (player instanceof ServerPlayerEntity) {
    // EnvironmentServerPacket.writeS2CSyncEnvPacket((ServerPlayerEntity) player, ((PlayerEnvAccess) player).isHotEnvAffected(), ((PlayerEnvAccess) player).isColdEnvAffected());
    // ((PlayerEnvAccess) player).compatSync();
    // }
    // }

}
