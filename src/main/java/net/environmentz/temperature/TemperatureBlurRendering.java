package net.environmentz.temperature;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class TemperatureBlurRendering {

    private static final Identifier BLUR_SHADER = EnvironmentzMain.identifierOf("shaders/post/blurring.json");

    @Nullable
    private static PostEffectProcessor blurProcessor;
    private static boolean loadFailed;
    private static int framebufferWidth;
    private static int framebufferHeight;

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return EnvironmentzMain.identifierOf("blur_shader");
            }

            @Override
            public void reload(ResourceManager manager) {
                close();
            }
        });
    }

    public static void render(MinecraftClient client, float tickDelta) {
        ClientPlayerEntity player = client.player;
        float progress = RenderInit.getBlurProgress();
        if (!ConfigInit.CONFIG.blurScreenEffect || progress <= 0.01F || player == null || player.isCreative() || player.isSpectator()) {
            return;
        }
        PostEffectProcessor processor = getProcessor(client);
        if (processor == null) {
            return;
        }
        Window window = client.getWindow();
        if (window.getFramebufferWidth() != framebufferWidth || window.getFramebufferHeight() != framebufferHeight) {
            framebufferWidth = window.getFramebufferWidth();
            framebufferHeight = window.getFramebufferHeight();
            processor.setupDimensions(framebufferWidth, framebufferHeight);
        }
        processor.setUniforms("Progress", progress);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        processor.render(tickDelta);
    }

    @Nullable
    private static PostEffectProcessor getProcessor(MinecraftClient client) {
        if (blurProcessor == null && !loadFailed) {
            try {
                blurProcessor = new PostEffectProcessor(client.getTextureManager(), client.getResourceManager(), client.getFramebuffer(), BLUR_SHADER);
                framebufferWidth = -1;
                framebufferHeight = -1;
            } catch (IOException | JsonSyntaxException exception) {
                loadFailed = true;
                EnvironmentzMain.LOGGER.warn("Failed to load shader: {}", BLUR_SHADER, exception);
            }
        }
        return blurProcessor;
    }

    private static void close() {
        if (blurProcessor != null) {
            blurProcessor.close();
            blurProcessor = null;
        }
        loadFailed = false;
    }
}
