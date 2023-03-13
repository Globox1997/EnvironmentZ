package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected boolean isShaking(AbstractClientPlayerEntity entity) {
        if (!entity.isCreative() && !entity.isSpectator() && !entity.isInvulnerable())
            if (((TemperatureManagerAccess) entity).getTemperatureManager().getPlayerTemperature() <= Temperatures.getBodyTemperatures(0)) {
                return true;
            }
        return super.isShaking(entity);
    }

}
