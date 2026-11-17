package net.environmentz.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements PlayerEnvAccess {

    @Unique
    public int compatSync = 0;

    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;tick()V", shift = Shift.AFTER))
    public void playerTickMixin(CallbackInfo info) {
        if (compatSync > 0) {
            compatSync--;
            if (compatSync == 1) {
                EnvironmentServerPacket.syncAll((ServerPlayer) (Object) this);
            }
        }
    }

    @Inject(method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V", at = @At(value = "TAIL"))
    public void copyFromMixinTwo(ServerPlayer oldPlayer, boolean alive, CallbackInfo info) {
        ((TemperatureManagerAccess) this).getTemperatureManager().copyFrom(((TemperatureManagerAccess) oldPlayer).getTemperatureManager(), alive);
        compatSync();
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("TAIL"))
    private void teleportMixin(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo info) {
        compatSync();
    }

    @Override
    public void compatSync() {
        compatSync = 5;
    }

}
