package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerEntityRendererMixin(Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected boolean isShaking(AbstractClientPlayer entity) {
        if (!entity.isCreative() && !entity.isSpectator() && !entity.isInvulnerable())
            if (((TemperatureManagerAccess) entity).getTemperatureManager().getPlayerTemperature() <= Temperatures.getBodyTemperature().maxVeryCold()) {
                return true;
            }
        return super.isShaking(entity);
    }

}
