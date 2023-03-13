package net.environmentz.mixin;

import com.mojang.authlib.GameProfile;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerEnvAccess {

    public int compatSync = 0;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "playerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", shift = Shift.AFTER))
    public void playerTickMixin(CallbackInfo info) {
        if (compatSync > 0) {
            compatSync--;
            if (compatSync == 1) {
                TemperatureManager temperatureManager = ((TemperatureManagerAccess) (ServerPlayerEntity) (Object) this).getTemperatureManager();
                EnvironmentServerPacket.writeS2CSyncEnvPacket((ServerPlayerEntity) (Object) this, temperatureManager.isHotEnvAffected(), temperatureManager.isColdEnvAffected());
            }
        }
    }

    @Inject(method = "Lnet/minecraft/server/network/ServerPlayerEntity;copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At(value = "TAIL"))
    public void copyFromMixinTwo(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        compatSync();
    }

    @Inject(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setWorld(Lnet/minecraft/server/world/ServerWorld;)V"))
    void teleportFix(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info) {
        compatSync();
    }

    @Nullable
    @Inject(method = "moveToWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;syncedFoodLevel:I", ordinal = 0))
    private void moveToWorldMixin(ServerWorld destination, CallbackInfoReturnable<Entity> info) {
        compatSync();
    }

    @Override
    public void compatSync() {
        compatSync = 5;
    }

}
