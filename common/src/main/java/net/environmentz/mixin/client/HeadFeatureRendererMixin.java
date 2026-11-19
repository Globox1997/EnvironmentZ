package net.environmentz.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.environmentz.EnvironmentzMain;
import net.environmentz.entity.model.WolfHelmetModel;
import net.environmentz.init.ItemInit;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public abstract class HeadFeatureRendererMixin<S extends LivingEntityRenderState, M extends EntityModel<S> & HeadedModel> extends RenderLayer<S, M> {

    @Unique
    private static final Identifier WOLF_HELMET = EnvironmentzMain.identifierOf("textures/entity/wolf_helmet.png");

    @Shadow
    @Final
    private CustomHeadLayer.Transforms transforms;

    @Unique
    private final WolfHelmetModel wolfHelmetModel = new WolfHelmetModel(WolfHelmetModel.getTexturedModelData().bakeRoot());

    public HeadFeatureRendererMixin(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void submitMixin(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int light, S state, float yRot, float xRot, CallbackInfo info) {
        if (!(state instanceof HumanoidRenderState humanoidState)) {
            return;
        }
        ItemStack itemStack = humanoidState.headEquipment;
        if (!itemStack.isEmpty() && itemStack.is(ItemInit.WOLF_HELMET)) {
            matrixStack.pushPose();
            matrixStack.scale(this.transforms.horizontalScale(), this.transforms.verticalScale(), this.transforms.horizontalScale());
            M parentModel = this.getParentModel();
            parentModel.root().translateAndRotate(matrixStack);
            parentModel.translateToHead(matrixStack);
            matrixStack.translate(0.0D, -1.75D, 0.0D);
            matrixStack.scale(1.18F, 1.18F, 1.18F);
            submitNodeCollector.submitModel(this.wolfHelmetModel, Unit.INSTANCE, matrixStack,
                    itemStack.hasFoil() ? RenderTypes.armorCutoutNoCullGlint(WOLF_HELMET) : RenderTypes.armorCutoutNoCull(WOLF_HELMET), light, OverlayTexture.NO_OVERLAY,
                    state.outlineColor);
            matrixStack.popPose();
            info.cancel();
        }
    }
}
