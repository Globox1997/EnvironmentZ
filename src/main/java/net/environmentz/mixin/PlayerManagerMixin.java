package net.environmentz.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL"))
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        TemperatureManager temperatureManager = ((TemperatureManagerAccess) player).getTemperatureManager();

        EnvironmentServerPacket.writeS2CSyncEnvPacket(player, temperatureManager.isHotEnvAffected(), temperatureManager.isColdEnvAffected());
        EnvironmentServerPacket.writeS2CTemperaturePacket(player, temperatureManager.getPlayerTemperature(), temperatureManager.getPlayerWetIntensityValue());
        EnvironmentServerPacket.writeS2CSyncValuesPacket(player);
    }

    @Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerRespawned(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void respawnPlayerMixin(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld,
            Optional<Vec3d> optional2, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {
        TemperatureManager temperatureManager = ((TemperatureManagerAccess) player).getTemperatureManager();

        EnvironmentServerPacket.writeS2CSyncEnvPacket(serverPlayerEntity, temperatureManager.isHotEnvAffected(), temperatureManager.isColdEnvAffected());
    }
}
