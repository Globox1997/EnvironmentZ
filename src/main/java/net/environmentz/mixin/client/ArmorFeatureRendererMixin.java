package net.environmentz.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.environmentz.init.ItemInit;
import net.environmentz.init.TagInit;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    @Unique
    @Nullable
    private LivingEntity livingEntity = null;

    public ArmorFeatureRendererMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(method = "render*", at = @At("HEAD"))
    private void renderMixin(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info) {
        this.livingEntity = livingEntity;
    }

    @Inject(method = "render*", at = @At("RETURN"))
    private void renderReturnMixin(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l,
                                   CallbackInfo info) {
        this.livingEntity = null;
    }

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"), cancellable = true)
    private void renderArmorMixin(PoseStack matrices, MultiBufferSource vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo info,
                                  @Local ItemStack itemStack, @Local boolean bl, @Local ArmorMaterial armorMaterial) {
        if (bl && armorSlot.equals(EquipmentSlot.LEGS) && itemStack.is(ItemInit.WANDERER_LEGGINGS)) {
            int color = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, -6265536));
            this.renderModel(matrices, vertexConsumers, light, model, color, armorMaterial.layers().getFirst().texture(false));
            if (itemStack.hasFoil()) {
                this.renderGlint(matrices, vertexConsumers, light, model);
            }
            info.cancel();
        }
    }

    @Inject(method = "usesInnerModel", at = @At("HEAD"), cancellable = true)
    private void usesInnerModelMixin(EquipmentSlot slot, CallbackInfoReturnable<Boolean> info) {
        if (this.livingEntity != null && this.livingEntity.getItemBySlot(slot).is(TagInit.SLIM_ARMOR)) {
            info.setReturnValue(true);
        }
    }

    @Shadow
    protected abstract void renderGlint(PoseStack matrices, MultiBufferSource vertexConsumers, int light, A model);

    @Shadow
    protected abstract void renderModel(PoseStack matrices, MultiBufferSource vertexConsumers, int light, A model, int i, ResourceLocation identifier);

}
