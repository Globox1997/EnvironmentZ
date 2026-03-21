package net.environmentz.init;


import net.environmentz.EnvironmentzMain;
import net.environmentz.entity.model.WolfHelmetModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;

@Environment(EnvType.CLIENT)
public class RenderInit {

    public static final EntityModelLayer WOLF_HELMET_LAYER = new EntityModelLayer(Identifier.of("environmentz:wolf_helmet_render_layer"), "wolf_helmet_render_layer");

    private static final ManagedShaderEffect blurringEffect = ShaderEffectManager.getInstance().manage(EnvironmentzMain.identifierOf("shaders/post/blurring.json"),
            shader -> shader.setUniformValue("Radius", 8f));
    private static final Uniform1f blurProgress = blurringEffect.findUniform1f("Progress");
    private static float blurProgressValue = 0.0F;
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(WOLF_HELMET_LAYER, WolfHelmetModel::getTexturedModelData);

        if (ConfigInit.CONFIG.blurScreenEffect) {
            ShaderEffectRenderCallback.EVENT.register((deltaTick) -> {
                if (blurProgressValue > 0.01F && client.player != null && !client.player.isCreative() && !client.player.isSpectator()) {
                    blurProgress.set(blurProgressValue);
                    blurringEffect.render(deltaTick);
                }
            });
        }

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : DyedColorComponent.getColor(stack, 0xffe3c88e), ItemInit.WANDERER_HELMET, ItemInit.WANDERER_CHESTPLATE,
                ItemInit.WANDERER_LEGGINGS, ItemInit.WANDERER_BOOTS);
    }

    public static void setBlurProgress(float blurProgress) {
        blurProgressValue = blurProgress;
    }

    public static float getBlurProgress() {
        return blurProgressValue;
    }
}
