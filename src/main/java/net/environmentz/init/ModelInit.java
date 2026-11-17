package net.environmentz.init;

import net.environmentz.EnvironmentzMain;
import net.environmentz.entity.model.WolfHelmetModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;

@Environment(EnvType.CLIENT)
public class ModelInit {

    public static final ModelLayerLocation WOLF_HELMET_LAYER = new ModelLayerLocation(EnvironmentzMain.identifierOf("wolf_helmet_render_layer"), "wolf_helmet_render_layer");
    public static final ArmorModelSet<ModelLayerLocation> SLIM_ARMOR_LAYERS = new ArmorModelSet<>(slimArmorLayer("head"), slimArmorLayer("chest"), slimArmorLayer("legs"),
            slimArmorLayer("feet"));

    private static final CubeDeformation SLIM_ARMOR_DEFORMATION = new CubeDeformation(0.5F);

    public static void init() {
        ModelLayerRegistry.registerModelLayer(WOLF_HELMET_LAYER, WolfHelmetModel::getTexturedModelData);
        ModelLayerRegistry.registerArmorModelLayers(SLIM_ARMOR_LAYERS,
                () -> HumanoidModel.createArmorMeshSet(SLIM_ARMOR_DEFORMATION, SLIM_ARMOR_DEFORMATION).map(mesh -> LayerDefinition.create(mesh, 64, 32)));
    }

    private static ModelLayerLocation slimArmorLayer(String part) {
        return new ModelLayerLocation(EnvironmentzMain.identifierOf("slim_armor"), part);
    }
}
