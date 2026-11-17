package net.environmentz.mixin.client;

import net.environmentz.init.ModelInit;
import net.environmentz.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> extends RenderLayer<S, M> {

    @Unique
    @Nullable
    private ArmorModelSet<A> slimModelSet = null;

    public ArmorFeatureRendererMixin(RenderLayerParent<S, M> context) {
        super(context);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "getArmorModel", at = @At("HEAD"), cancellable = true)
    private void getArmorModelMixin(S state, EquipmentSlot slot, CallbackInfoReturnable<A> info) {
        if (state.isBaby || slot == EquipmentSlot.LEGS) {
            return;
        }
        ItemStack itemStack = switch (slot) {
            case HEAD -> state.headEquipment;
            case CHEST -> state.chestEquipment;
            case FEET -> state.feetEquipment;
            default -> ItemStack.EMPTY;
        };
        if (itemStack.is(TagInit.SLIM_ARMOR)) {
            if (this.slimModelSet == null) {
                this.slimModelSet = (ArmorModelSet<A>) (ArmorModelSet<?>) ArmorModelSet.bake(ModelInit.SLIM_ARMOR_LAYERS, Minecraft.getInstance().getEntityModels(), HumanoidModel::new);
            }
            info.setReturnValue(this.slimModelSet.get(slot));
        }
    }

}
