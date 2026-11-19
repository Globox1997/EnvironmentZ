package net.environmentz.fabric.client;

import net.environmentz.EnvironmentzMain;
import net.environmentz.block.screen.CopperHeaterScreen;
import net.environmentz.init.BlockInit;
import net.environmentz.init.ModelInit;
import net.environmentz.init.RenderInit;
import net.environmentz.network.EnvironmentClientPacket;
import net.environmentz.network.packet.AffectionPacket;
import net.environmentz.network.packet.SyncValuesPacket;
import net.environmentz.network.packet.TemperaturePacket;
import net.environmentz.network.packet.ThermometerPacket;
import net.environmentz.temperature.TemperatureHudRendering;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public class EnvironmentzFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, CopperHeaterScreen::new);
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, EnvironmentzMain.identifierOf("temperature"),
                (context, tickCounter) -> TemperatureHudRendering.render(context));
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> RenderInit.appendTooltip(stack, lines));

        ClientPlayNetworking.registerGlobalReceiver(AffectionPacket.PACKET_ID,
                (payload, context) -> context.client().execute(() -> EnvironmentClientPacket.handleAffection(payload)));
        ClientPlayNetworking.registerGlobalReceiver(TemperaturePacket.PACKET_ID,
                (payload, context) -> context.client().execute(() -> EnvironmentClientPacket.handleTemperature(payload)));
        ClientPlayNetworking.registerGlobalReceiver(ThermometerPacket.PACKET_ID,
                (payload, context) -> context.client().execute(() -> EnvironmentClientPacket.handleThermometer(payload)));
        ClientPlayNetworking.registerGlobalReceiver(SyncValuesPacket.PACKET_ID,
                (payload, context) -> context.client().execute(() -> EnvironmentClientPacket.handleSyncValues(payload)));

        ModelInit.registerLayers((location, definition) -> ModelLayerRegistry.registerModelLayer(location, definition::get));
    }

}
