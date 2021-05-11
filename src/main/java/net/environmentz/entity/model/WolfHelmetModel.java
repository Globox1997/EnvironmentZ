package net.environmentz.entity.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class WolfHelmetModel extends Model {
    private final ModelPart Helmet;
    private final ModelPart FurSides;
    private final ModelPart Snout;
    private final ModelPart Ears;
    private final ModelPart Left;
    private final ModelPart EarTip_r1;
    private final ModelPart Right;
    private final ModelPart EarTip_r2;

    public WolfHelmetModel() {
        super(RenderLayer::getEntityCutout);
        Helmet = new ModelPart(64, 32, 0, 0);
        Helmet.setPivot(0.0F, 24.0F, 0.0F);
        Helmet.setTextureOffset(0, 0).addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        FurSides = new ModelPart(64, 32, 0, 0);
        FurSides.setPivot(0.0F, 24.0F, 0.0F);
        FurSides.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -4.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
        FurSides.setTextureOffset(0, 0).addCuboid(3.0F, -4.0F, -4.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

        Snout = new ModelPart(64, 32, 0, 0);
        Snout.setPivot(0.0F, 24.0F, 0.0F);
        Snout.setTextureOffset(0, 16).addCuboid(-2.0F, -5.0F, -7.0F, 4.0F, 2.0F, 3.0F, 0.0F, false);

        Ears = new ModelPart(64, 32, 0, 0);
        Ears.setPivot(0.0F, 24.0F, 0.0F);

        Left = new ModelPart(64, 32, 0, 0);
        Left.setPivot(-8.0F, -8.0F, 8.0F);
        Ears.addChild(Left);

        EarTip_r1 = new ModelPart(64, 32, 0, 0);
        EarTip_r1.setPivot(10.5F, 0.0F, -8.0F);
        Left.addChild(EarTip_r1);
        EarTip_r1.setTextureOffset(43, 3).addCuboid(-1.5F, -3.55F, -1.0F, 2.0F, 1.0F, 1.0F, -0.0001F, false);
        EarTip_r1.setTextureOffset(32, 0).addCuboid(-1.5F, -2.55F, -1.0F, 3.0F, 3.0F, 2.0F, -0.001F, false);

        Right = new ModelPart(64, 32, 0, 0);
        Right.setPivot(-8.0F, -8.0F, 8.0F);
        Ears.addChild(Right);

        EarTip_r2 = new ModelPart(64, 32, 0, 0);
        EarTip_r2.setPivot(10.5F, 0.0F, -8.0F);
        Right.addChild(EarTip_r2);
        EarTip_r2.setTextureOffset(43, 0).addCuboid(-5.5F, -3.55F, -1.0F, 2.0F, 1.0F, 1.0F, -0.0001F, false);
        EarTip_r2.setTextureOffset(32, 5).addCuboid(-6.5F, -2.55F, -1.0F, 3.0F, 3.0F, 2.0F, -0.001F, false);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        this.EarTip_r1.pitch = -0.3927F;
        this.EarTip_r2.pitch = -0.3927F;

        Helmet.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        FurSides.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        Snout.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        Ears.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

}
