package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.environmentz.access.PlayerEnvAccess;
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
            if (((PlayerEnvAccess) entity).getPlayerTemperature() <= -240)
                return true;

        return super.isShaking(entity);
    }

}
