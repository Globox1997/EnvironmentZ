package net.environmentz.init;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RenderInit {

    private static float blurProgressValue = 0.0F;

    public static void appendTooltip(ItemStack stack, List<Component> lines) {
        if (stack.has(ItemInit.INSULATED) && stack.get(ItemInit.INSULATED)) {
            lines.add(Component.translatable("item.environmentz.fur_insolated.tooltip").withStyle(ChatFormatting.BLUE));
        }
        if (stack.has(ItemInit.ICED)) {
            Integer iced = stack.get(ItemInit.ICED);
            if (iced != null && iced > 0) {
                lines.add(Component.translatable("item.environmentz.iced.tooltip", iced).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    public static void setBlurProgress(float blurProgress) {
        blurProgressValue = blurProgress;
    }

    public static float getBlurProgress() {
        return blurProgressValue;
    }
}
