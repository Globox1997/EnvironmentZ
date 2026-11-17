package net.environmentz.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.environmentz.EnvironmentzMain;
import net.environmentz.entity.model.WolfHelmetModel;
import net.environmentz.init.ItemInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CustomHeadLayer.class)
public class HeadFeatureRendererMixin {

    @Unique
    private static final ResourceLocation WOLF_HELMET = EnvironmentzMain.identifierOf("textures/entity/wolf_helmet.png");

    @Shadow
    @Final
    private float scaleX;
    @Shadow
    @Final
    private float scaleY;
    @Shadow
    @Final
    private float scaleZ;

    @Unique
    private final WolfHelmetModel wolfHelmetModel = new WolfHelmetModel(WolfHelmetModel.getTexturedModelData().bakeRoot());

    @SuppressWarnings("rawtypes")
    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l,
                       CallbackInfo info) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (!itemStack.isEmpty() && (itemStack.getItem() == ItemInit.WOLF_HELMET)) {
            matrixStack.pushPose();
            matrixStack.scale(this.scaleX, this.scaleY, this.scaleZ);
            ((HeadedModel) ((CustomHeadLayer) (Object) this).getParentModel()).getHead().translateAndRotate(matrixStack);
            matrixStack.translate(0.0D, -1.75D, 0.0D);
            matrixStack.scale(1.18F, 1.18F, 1.18F);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(vertexConsumerProvider, this.wolfHelmetModel.renderType(WOLF_HELMET),
                    false, itemStack.hasFoil());
            this.wolfHelmetModel.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
            info.cancel();
        }
    }
}
