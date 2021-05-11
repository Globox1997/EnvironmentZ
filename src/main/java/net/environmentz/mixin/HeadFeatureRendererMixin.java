package net.environmentz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.environmentz.entity.model.WolfHelmetModel;
import net.environmentz.init.ItemInit;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(HeadFeatureRenderer.class)
public class HeadFeatureRendererMixin {
  @Shadow
  private float field_24474;
  @Shadow
  private float field_24475;
  @Shadow
  private float field_24476;

  private final WolfHelmetModel wolfHelmetModel = new WolfHelmetModel();

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
      LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info) {
    ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
    if (!(itemStack.isEmpty()) && (itemStack.getItem() == ItemInit.WOLF_HELMET)) {
      matrixStack.push();
      matrixStack.scale(this.field_24474, this.field_24475, this.field_24476);
      ((ModelWithHead) ((HeadFeatureRenderer) (Object) this).getContextModel()).getHead().rotate(matrixStack);
      matrixStack.translate(0.0D, -1.75D, 0.0D);
      matrixStack.scale(1.18F, 1.18F, 1.18F);
      VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider,
          this.wolfHelmetModel.getLayer(new Identifier("environmentz", "textures/entity/wolf_helmet.png")), false,
          itemStack.hasGlint());
      this.wolfHelmetModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.pop();
      info.cancel();
    }
  }
}
