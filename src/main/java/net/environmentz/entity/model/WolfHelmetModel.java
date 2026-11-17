package net.environmentz.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class WolfHelmetModel extends Model {
    private final ModelPart helmet;
    private final ModelPart furSides;
    private final ModelPart snout;
    private final ModelPart ears;
    private final ModelPart left;
    private final ModelPart earTip_r1;
    private final ModelPart right;
    private final ModelPart earTip_r2;

    public WolfHelmetModel(ModelPart root) {
        super(RenderType::armorCutoutNoCull);
        this.helmet = root.getChild("helmet");
        this.furSides = root.getChild("furSides");
        this.snout = root.getChild("snout");
        this.ears = root.getChild("ears");
        this.left = this.ears.getChild("left");
        this.earTip_r1 = this.left.getChild("earTip_r1");
        this.right = this.ears.getChild("right");
        this.earTip_r2 = this.right.getChild("earTip_r2");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        modelPartData.addOrReplaceChild("furSides", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 1.0F, 5.0F, 1.0F).addBox(3.0F, -4.0F, -4.0F, 1.0F, 5.0F, 1.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        modelPartData.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -5.0F, -7.0F, 4.0F, 2.0F, 3.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition modelPartData2 = modelPartData.addOrReplaceChild("ears", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        modelPartData2.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(-8.0F, -8.0F, 8.0F)).addOrReplaceChild("earTip_r1", CubeListBuilder.create().texOffs(43, 3)
                        .addBox(-1.5F, -3.55F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.0001F)).texOffs(32, 0).addBox(-1.5F, -2.55F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(-0.001F)),
                PartPose.offset(10.5F, 0.0F, -8.0F));
        modelPartData2.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.offset(-8.0F, -8.0F, 8.0F)).addOrReplaceChild("earTip_r2", CubeListBuilder.create().texOffs(43, 0)
                        .addBox(-5.5F, -3.55F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.0001F)).texOffs(32, 5).addBox(-6.5F, -2.55F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(-0.001F)),
                PartPose.offset(10.5F, 0.0F, -8.0F));
        return LayerDefinition.create(modelData, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        this.earTip_r1.xRot = -0.3927F;
        this.earTip_r2.xRot = -0.3927F;

        helmet.render(matrices, vertexConsumer, light, overlay, color);
        furSides.render(matrices, vertexConsumer, light, overlay, color);
        snout.render(matrices, vertexConsumer, light, overlay, color);
        ears.render(matrices, vertexConsumer, light, overlay, color);
    }


}
