package net.environmentz.init;


import net.environmentz.EnvironmentzMain;
import net.environmentz.block.screen.CopperHeaterScreen;
import net.environmentz.entity.model.WolfHelmetModel;
import net.environmentz.temperature.TemperatureBlurRendering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.DyedItemColor;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final ModelLayerLocation WOLF_HELMET_LAYER = new ModelLayerLocation(EnvironmentzMain.identifierOf("wolf_helmet_render_layer"), "wolf_helmet_render_layer");

    private static float blurProgressValue = 0.0F;

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(WOLF_HELMET_LAYER, WolfHelmetModel::getTexturedModelData);

        TemperatureBlurRendering.init();

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, 0xffffffff), ItemInit.WANDERER_HELMET, ItemInit.WANDERER_CHESTPLATE,
                ItemInit.WANDERER_LEGGINGS, ItemInit.WANDERER_BOOTS);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, 0xffffffff), ItemInit.WOOL_HELMET, ItemInit.WOOL_CHESTPLATE,
                ItemInit.WOOL_LEGGINGS, ItemInit.WOOL_BOOTS);

        MenuScreens.register(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, CopperHeaterScreen::new);

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.has(ItemInit.INSULATED) && stack.get(ItemInit.INSULATED)) {
                lines.add(Component.translatable("item.environmentz.fur_insolated.tooltip").withStyle(ChatFormatting.BLUE));
            }
            if (stack.has(ItemInit.ICED)) {
                Integer iced = stack.get(ItemInit.ICED);
                if (iced != null && iced > 0) {
                    lines.add(Component.translatable("item.environmentz.iced.tooltip", iced).withStyle(ChatFormatting.BLUE));
                }
            }
        });

    }

    public static void setBlurProgress(float blurProgress) {
        blurProgressValue = blurProgress;
    }

    public static float getBlurProgress() {
        return blurProgressValue;
    }
}
