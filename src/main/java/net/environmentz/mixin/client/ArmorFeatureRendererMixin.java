package net.environmentz.mixin.client;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
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

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow
    @Mutable
    @Final
    private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

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

    @Inject(method = "usesInnerModel", at = @At("HEAD"), cancellable = true)
    private void usesInnerModelMixin(EquipmentSlot slot, CallbackInfoReturnable<Boolean> info) {
        if (this.livingEntity != null && livingEntity.getEquippedStack(slot).isIn(TagInit.SLIM_ARMOR)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
    private void getArmorTextureMixin(ArmorItem item, boolean secondLayer, @Nullable String overlay, CallbackInfoReturnable<Identifier> info) {
        if (secondLayer && overlay == null && item.getSlotType() == EquipmentSlot.LEGS && item.getDefaultStack().isIn(TagInit.SLIM_ARMOR)) {
            String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + 2 + "_legs.png";
            info.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new));
        }
    }

}
