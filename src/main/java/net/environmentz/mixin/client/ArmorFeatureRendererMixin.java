package net.environmentz.mixin.client;

import java.util.Map;

import net.environmentz.init.ItemInit;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.environmentz.init.TagInit;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow
    @Mutable
    @Final
    private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

    @Unique
    private LivingEntity livingEntity = null;

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info) {
        if (this.livingEntity == null) {
            this.livingEntity = livingEntity;
        }
    }

    @Inject(method = "renderArmor", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void renderArmorMixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo info, ItemStack itemStack, ArmorItem armorItem, boolean bl, ArmorMaterial armorMaterial) {
        if (bl && armorSlot.equals(EquipmentSlot.LEGS) && itemStack.isOf(ItemInit.WANDERER_LEGGINGS)) {
            int j = ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(itemStack, -6265536));
            this.renderArmorParts(matrices, vertexConsumers, light, model, j, armorMaterial.layers().getFirst().getTexture(!bl));
            if (itemStack.hasGlint()) {
                this.renderGlint(matrices, vertexConsumers, light, model);
            }
            info.cancel();
        }
    }

    @Inject(method = "usesInnerModel", at = @At("HEAD"), cancellable = true)
    private void usesInnerModelMixin(EquipmentSlot slot, CallbackInfoReturnable<Boolean> info) {
        if (this.livingEntity != null && this.livingEntity.getEquippedStack(slot).isIn(TagInit.SLIM_ARMOR)) {
            info.setReturnValue(true);
        }
    }

    @Shadow
    abstract void renderGlint(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model);

    @Shadow
    protected abstract void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, A model, int i, Identifier identifier);

}
