package net.environmentz.init;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.environmentz.EnvironmentzMain;
import net.environmentz.entity.model.WolfHelmetModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ArmorModelSet;

public class ModelInit {

    public static final ModelLayerLocation WOLF_HELMET_LAYER = new ModelLayerLocation(EnvironmentzMain.identifierOf("wolf_helmet_render_layer"), "wolf_helmet_render_layer");
    public static final ArmorModelSet<ModelLayerLocation> SLIM_ARMOR_LAYERS = new ArmorModelSet<>(slimArmorLayer("head"), slimArmorLayer("chest"), slimArmorLayer("legs"),
            slimArmorLayer("feet"));

    private static final CubeDeformation SLIM_ARMOR_DEFORMATION = new CubeDeformation(0.5F);

    public static void registerLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registrar) {
        registrar.accept(WOLF_HELMET_LAYER, WolfHelmetModel::getTexturedModelData);
        registrar.accept(SLIM_ARMOR_LAYERS.head(), () -> createSlimArmorLayers().head());
        registrar.accept(SLIM_ARMOR_LAYERS.chest(), () -> createSlimArmorLayers().chest());
        registrar.accept(SLIM_ARMOR_LAYERS.legs(), () -> createSlimArmorLayers().legs());
        registrar.accept(SLIM_ARMOR_LAYERS.feet(), () -> createSlimArmorLayers().feet());
    }

    private static ArmorModelSet<LayerDefinition> createSlimArmorLayers() {
        return HumanoidModel.createArmorMeshSet(SLIM_ARMOR_DEFORMATION, SLIM_ARMOR_DEFORMATION).map(mesh -> LayerDefinition.create(mesh, 64, 32));
    }

    private static ModelLayerLocation slimArmorLayer(String part) {
        return new ModelLayerLocation(EnvironmentzMain.identifierOf("slim_armor"), part);
    }
}
