package net.environmentz.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.environmentz.EnvironmentzMain;
import net.environmentz.block.screen.CopperHeaterScreen;
import net.environmentz.config.EnvironmentzConfig;
import net.environmentz.init.BlockInit;
import net.environmentz.init.ModelInit;
import net.environmentz.init.RenderInit;
import net.environmentz.network.EnvironmentClientPacket;
import net.environmentz.network.packet.AffectionPacket;
import net.environmentz.network.packet.SyncValuesPacket;
import net.environmentz.network.packet.TemperaturePacket;
import net.environmentz.network.packet.ThermometerPacket;
import net.environmentz.temperature.TemperatureHudRendering;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = EnvironmentzMain.MOD_ID, dist = Dist.CLIENT)
public class EnvironmentzNeoForgeClient {

    public EnvironmentzNeoForgeClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterMenuScreensEvent.class, event -> event.register(BlockInit.COPPER_HEATER_SCREEN_HANDLER_TYPE, CopperHeaterScreen::new));
        modBus.addListener(RegisterGuiLayersEvent.class, event -> event.registerBelow(VanillaGuiLayers.HOTBAR, EnvironmentzMain.identifierOf("temperature"),
                (context, deltaTracker) -> TemperatureHudRendering.render(context)));
        NeoForge.EVENT_BUS.addListener(ItemTooltipEvent.class, event -> RenderInit.appendTooltip(event.getItemStack(), event.getToolTip()));

        modBus.addListener(RegisterClientPayloadHandlersEvent.class, event -> {
            event.register(AffectionPacket.PACKET_ID, (payload, context) -> EnvironmentClientPacket.handleAffection(payload));
            event.register(TemperaturePacket.PACKET_ID, (payload, context) -> EnvironmentClientPacket.handleTemperature(payload));
            event.register(ThermometerPacket.PACKET_ID, (payload, context) -> EnvironmentClientPacket.handleThermometer(payload));
            event.register(SyncValuesPacket.PACKET_ID, (payload, context) -> EnvironmentClientPacket.handleSyncValues(payload));
        });

        modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> ModelInit.registerLayers(event::registerLayerDefinition));

        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> AutoConfigClient.getConfigScreen(EnvironmentzConfig.class, parent).get());
    }

}
