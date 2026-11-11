package net.environmentz.init;


import net.environmentz.EnvironmentzMain;
import net.environmentz.block.screen.CopperHeaterScreen;
import net.environmentz.entity.model.WolfHelmetModel;
import net.environmentz.temperature.TemperatureBlurRendering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.component.type.DyedColorComponent;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final EntityModelLayer WOLF_HELMET_LAYER = new EntityModelLayer(EnvironmentzMain.identifierOf("wolf_helmet_render_layer"), "wolf_helmet_render_layer");

    private static float blurProgressValue = 0.0F;

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(WOLF_HELMET_LAYER, WolfHelmetModel::getTexturedModelData);

        TemperatureBlurRendering.init();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : DyedColorComponent.getColor(stack, 0xffffffff), ItemInit.WANDERER_HELMET, ItemInit.WANDERER_CHESTPLATE,
                ItemInit.WANDERER_LEGGINGS, ItemInit.WANDERER_BOOTS);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : DyedColorComponent.getColor(stack, 0xffffffff), ItemInit.WOOL_HELMET, ItemInit.WOOL_CHESTPLATE,
                ItemInit.WOOL_LEGGINGS, ItemInit.WOOL_BOOTS);

        HandledScreens.register(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, CopperHeaterScreen::new);

    }

    public static void setBlurProgress(float blurProgress) {
        blurProgressValue = blurProgress;
    }

    public static float getBlurProgress() {
        return blurProgressValue;
    }
}
