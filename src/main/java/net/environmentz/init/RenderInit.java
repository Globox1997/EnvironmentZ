package net.environmentz.init;

import net.environmentz.entity.model.WolfHelmetModel;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class RenderInit {
    public static final EntityModelLayer WOLF_HELMET_LAYER = new EntityModelLayer(new Identifier("environmentz:wolf_helmet_render_layer"), "wolf_helmet_render_layer");

    public static void init(){
        EntityModelLayerRegistry.registerModelLayer(WOLF_HELMET_LAYER, WolfHelmetModel::getTexturedModelData);
    }
}