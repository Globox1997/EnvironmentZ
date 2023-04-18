package net.environmentz.mixin.compat;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

// import io.github.apace100.apoli.power.PowerTypeReference;
// import io.github.apace100.origins.Origins;
// import io.github.apace100.origins.command.OriginCommand;
// import io.github.apace100.origins.origin.Origin;
// import io.github.apace100.origins.origin.OriginLayer;
import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@SuppressWarnings("unused")
// @Mixin(OriginCommand.class)
public class OriginCommandMixin {

    // @Inject(method = "setOrigin", at = @At(value = "INVOKE", target = "Lio/github/apace100/origins/component/OriginComponent;sync()V"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    // private static void setOriginMixin(CommandContext<ServerCommandSource> commandContext, CallbackInfoReturnable<Integer> info, Collection targets, OriginLayer originLayer, Origin origin,
    // ServerCommandSource serverCommandSource, int processedTargets, Iterator var6, ServerPlayerEntity target) throws CommandSyntaxException {

    // if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("fire_immunity")))) {
    // if (((PlayerEnvAccess) target).isHotEnvAffected()) {
    // ((PlayerEnvAccess) target).setHotEnvAffected(false);
    // }
    // } else if (!((PlayerEnvAccess) target).isHotEnvAffected()) {
    // ((PlayerEnvAccess) target).setHotEnvAffected(true);
    // }
    // if (origin.hasPowerType(new PowerTypeReference<>(Origins.identifier("freeze")))) {
    // if (((PlayerEnvAccess) target).isColdEnvAffected()) {
    // ((PlayerEnvAccess) target).setColdEnvAffected(false);
    // }
    // } else if (!((PlayerEnvAccess) target).isColdEnvAffected()) {
    // ((PlayerEnvAccess) target).setColdEnvAffected(true);
    // }
    // EnvironmentServerPacket.writeS2CSyncEnvPacket(target, ((PlayerEnvAccess) target).isHotEnvAffected(), ((PlayerEnvAccess) target).isColdEnvAffected());
    // ((PlayerEnvAccess) target).compatSync();
    // }
}
