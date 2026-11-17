package net.environmentz.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.init.ConfigInit;
import net.environmentz.temperature.Temperatures;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @Unique
    private static final Identifier COLDNESS_OVERLAY = Identifier.parse("environmentz:textures/gui/coldness_overlay.png");
    @Unique
    private static float smoothFreezingRendering;
    @Unique
    private static final int FADE_INTERVAL_TICKS = 5;
    @Unique
    private static long lastFadeTime;

    @Inject(method = "submit", at = @At(value = "TAIL"))
    private void renderOverlaysMixin(float partialTicks, SubmitNodeCollector submitNodeCollector, PlayerRenderState playerRenderState, CameraRenderState cameraRenderState,
                                     boolean hideGui, CallbackInfo info) {
        Minecraft minecraftClient = Minecraft.getInstance();
        Player playerEntity = minecraftClient.player;
        if (!cameraRenderState.isFirstPerson || cameraRenderState.entityRenderState.isSleeping || !playerRenderState.hasPlayer) {
            return;
        }
        if (playerEntity != null && !playerEntity.isCreative() && !playerEntity.isSpectator() && ConfigInit.CONFIG.coldOverlay) {
            long worldTime = playerEntity.level().getGameTime();
            if (worldTime < lastFadeTime || worldTime - lastFadeTime >= FADE_INTERVAL_TICKS) {
                lastFadeTime = worldTime;
                if (playerEntity.level().getBiome(playerEntity.blockPosition()).value().getBaseTemperature() <= Temperatures.getBiomeTemperature().veryCold()
                        && ((TemperatureManagerAccess) playerEntity).getTemperatureManager().getPlayerColdProtectionAmount() <= 0) {

                    float maxWhitening = 0.3F;
                    if (playerEntity.level().isRaining()) {
                        maxWhitening = 0.5F;
                    }
                    if (smoothFreezingRendering < maxWhitening) {
                        smoothFreezingRendering = smoothFreezingRendering + 0.02F;
                    }
                } else if (smoothFreezingRendering > 0.0F) {
                    smoothFreezingRendering = smoothFreezingRendering - 0.02F;
                }
            }
            if (smoothFreezingRendering > 0.01F) {
                renderWinterOverlay(playerEntity, submitNodeCollector, smoothFreezingRendering);
            }
        }
    }

    @Unique
    private static void renderWinterOverlay(Player player, SubmitNodeCollector submitNodeCollector, float smooth) {
        BlockPos blockPos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        float f = Lightmap.getBrightness(player.level().dimensionType(), player.level().getMaxLocalRawBrightness(blockPos));
        int color = ARGB.colorFromFloat(smooth, f, f, f);
        float m = -player.getYRot() / 64.0f;
        float n = player.getXRot() / 64.0f;
        submitNodeCollector.submitCustomGeometry(new PoseStack(), RenderTypes.blockScreenEffect(COLDNESS_OVERLAY), (pose, builder) -> {
            builder.addVertex(pose.pose(), -1.0f, -1.0f, -0.5f).setUv(4.0f + m, 4.0f + n).setColor(color);
            builder.addVertex(pose.pose(), 1.0f, -1.0f, -0.5f).setUv(0.0f + m, 4.0f + n).setColor(color);
            builder.addVertex(pose.pose(), 1.0f, 1.0f, -0.5f).setUv(0.0f + m, 0.0f + n).setColor(color);
            builder.addVertex(pose.pose(), -1.0f, 1.0f, -0.5f).setUv(4.0f + m, 0.0f + n).setColor(color);
        });
    }

}
