package net.environmentz.temperature;

import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class TemperatureBlurRendering {

    private static final int MAX_BLUR_RADIUS = 8;
    private static final Identifier[] BLUR_EFFECTS = new Identifier[MAX_BLUR_RADIUS + 1];

    static {
        for (int radius = 1; radius <= MAX_BLUR_RADIUS; radius++) {
            BLUR_EFFECTS[radius] = EnvironmentzMain.identifierOf("blurring_" + radius);
        }
    }

    @Nullable
    public static Identifier getPostEffect(Minecraft client) {
        LocalPlayer player = client.player;
        float progress = RenderInit.getBlurProgress();
        if (!ConfigInit.CONFIG.blurScreenEffect || progress <= 0.01F || player == null || player.isCreative() || player.isSpectator()) {
            return null;
        }
        int radius = Mth.clamp(Mth.floor(MAX_BLUR_RADIUS * progress), 0, MAX_BLUR_RADIUS);
        return radius > 0 ? BLUR_EFFECTS[radius] : null;
    }
}
