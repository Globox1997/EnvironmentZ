package net.environmentz.init;


import net.environmentz.EnvironmentzMain;
import net.environmentz.block.screen.CopperHeaterScreen;
import net.environmentz.temperature.TemperatureHudRendering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class RenderInit {

    private static float blurProgressValue = 0.0F;

    public static void init() {
        MenuScreens.register(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, CopperHeaterScreen::new);

        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, EnvironmentzMain.identifierOf("temperature"),
                (context, tickCounter) -> TemperatureHudRendering.render(context));

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
