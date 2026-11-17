package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin<A extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<A, AvatarRenderState, PlayerModel> {

    @Unique
    private static final RenderStateDataKey<Boolean> FREEZING = RenderStateDataKey.create(() -> "environmentz:freezing");

    public PlayerEntityRendererMixin(Context ctx, PlayerModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void extractRenderStateMixin(A entity, AvatarRenderState state, float partialTicks, CallbackInfo info) {
        boolean freezing = entity instanceof Player player && !player.isCreative() && !player.isSpectator() && !player.isInvulnerable()
                && ((TemperatureManagerAccess) player).getTemperatureManager().getPlayerTemperature() <= Temperatures.getBodyTemperature().maxVeryCold();
        state.setData(FREEZING, freezing);
    }

    @Override
    protected boolean isShaking(AvatarRenderState state) {
        if (state.getDataOrDefault(FREEZING, false)) {
            return true;
        }
        return super.isShaking(state);
    }

}
