package net.environmentz.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class WolfHelmetModel extends Model {
    private final ModelPart root;
    private final ModelPart helmet;
    private final ModelPart furSides;
    private final ModelPart snout;
    private final ModelPart ears;
    private final ModelPart left;
    private final ModelPart earTip_r1;
    private final ModelPart right;
    private final ModelPart earTip_r2;

    public WolfHelmetModel(ModelPart root) {
        super(RenderLayer::getArmorCutoutNoCull);
        this.root = root;
        this.helmet = root.getChild("helmet");
        this.furSides = root.getChild("furSides");
        this.snout = root.getChild("snout");
        this.ears = root.getChild("ears");
        this.left = this.ears.getChild("left");
        this.earTip_r1 = this.left.getChild("earTip_r1");
        this.right = this.ears.getChild("right");
        this.earTip_r2 = this.right.getChild("earTip_r2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("helmet", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        modelPartData.addChild("furSides", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 1.0F, 5.0F, 1.0F).cuboid(3.0F, -4.0F, -4.0F, 1.0F, 5.0F, 1.0F),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        modelPartData.addChild("snout", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -5.0F, -7.0F, 4.0F, 2.0F, 3.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData modelPartData2 = modelPartData.addChild("ears", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        modelPartData2.addChild("left", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, -8.0F, 8.0F)).addChild("earTip_r1",
                ModelPartBuilder.create().uv(43, 3).cuboid(-1.5F, -3.55F, -1.0F, 2.0F, 1.0F, 1.0F).uv(32, 0).cuboid(-1.5F, -2.55F, -1.0F, 3.0F, 3.0F, 2.0F), ModelTransform.pivot(10.5F, 0.0F, -8.0F));
        modelPartData2.addChild("right", ModelPartBuilder.create(), ModelTransform.pivot(-8.0F, -8.0F, 8.0F)).addChild("earTip_r2",
                ModelPartBuilder.create().uv(43, 0).cuboid(-5.5F, -3.55F, -1.0F, 2.0F, 1.0F, 1.0F).uv(32, 5).cuboid(-6.5F, -2.55F, -1.0F, 3.0F, 3.0F, 2.0F), ModelTransform.pivot(10.5F, 0.0F, -8.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.earTip_r1.pitch = -0.3927F;
        this.earTip_r2.pitch = -0.3927F;

        helmet.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        furSides.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        snout.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        ears.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

}
