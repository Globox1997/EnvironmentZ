package net.environmentz.temperature;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.environmentz.EnvironmentzMain;
import net.environmentz.init.ConfigInit;
import net.environmentz.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class TemperatureBlurRendering {

    private static final ResourceLocation BLUR_SHADER = EnvironmentzMain.identifierOf("shaders/post/blurring.json");

    @Nullable
    private static PostChain blurProcessor;
    private static boolean loadFailed;
    private static int framebufferWidth;
    private static int framebufferHeight;

    public static void init() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return EnvironmentzMain.identifierOf("blur_shader");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                close();
            }
        });
    }

    public static void render(Minecraft client, float tickDelta) {
        LocalPlayer player = client.player;
        float progress = RenderInit.getBlurProgress();
        if (!ConfigInit.CONFIG.blurScreenEffect || progress <= 0.01F || player == null || player.isCreative() || player.isSpectator()) {
            return;
        }
        PostChain processor = getProcessor(client);
        if (processor == null) {
            return;
        }
        Window window = client.getWindow();
        if (window.getWidth() != framebufferWidth || window.getHeight() != framebufferHeight) {
            framebufferWidth = window.getWidth();
            framebufferHeight = window.getHeight();
            processor.resize(framebufferWidth, framebufferHeight);
        }
        processor.setUniform("Progress", progress);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        processor.process(tickDelta);
    }

    @Nullable
    private static PostChain getProcessor(Minecraft client) {
        if (blurProcessor == null && !loadFailed) {
            try {
                blurProcessor = new PostChain(client.getTextureManager(), client.getResourceManager(), client.getMainRenderTarget(), BLUR_SHADER);
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
