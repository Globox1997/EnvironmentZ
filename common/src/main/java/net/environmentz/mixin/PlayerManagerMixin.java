package net.environmentz.mixin;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.EffectInit;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void onPlayerConnectComfortMixin(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo info) {
        if (!((TemperatureManagerAccess) player).getTemperatureManager().isLoadedFromSave() && !player.isCreative() && ConfigInit.CONFIG.startUpComfortEffectDuration > 0) {
            player.addEffect(new MobEffectInstance(EffectInit.COMFORT, ConfigInit.CONFIG.startUpComfortEffectDuration, 0, false, false, true));
        }
    }
}
