package net.environmentz.neoforge;

import net.environmentz.EnvironmentzMain;
import net.environmentz.init.BlockInit;
import net.environmentz.init.CommandInit;
import net.environmentz.init.EffectInit;
import net.environmentz.init.EventInit;
import net.environmentz.init.ItemInit;
import net.environmentz.init.LoaderInit;
import net.environmentz.init.LootInit;
import net.environmentz.network.packet.AffectionPacket;
import net.environmentz.network.packet.SyncValuesPacket;
import net.environmentz.network.packet.TemperaturePacket;
import net.environmentz.network.packet.ThermometerPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(EnvironmentzMain.MOD_ID)
public class EnvironmentzNeoForge {

    public EnvironmentzNeoForge(IEventBus modBus) {
        EnvironmentzMain.init();

        modBus.addListener(RegisterEvent.class, EnvironmentzNeoForge::register);
        modBus.addListener(RegisterPayloadHandlersEvent.class, EnvironmentzNeoForge::registerPayloads);
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, event -> {
            if (event.getTabKey().equals(ItemInit.ENVIRONMENTZ_ITEM_GROUP)) {
                ItemInit.getCreativeTabItems().forEach(event::accept);
            }
        });

        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> CommandInit.register(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class, event -> LoaderInit.register(event::addListener));
        NeoForge.EVENT_BUS.addListener(LootTableLoadEvent.class, event -> LootInit.modifyLootTable(event.getKey(), event.getTable()::addPool));

        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerChangedDimensionEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                EventInit.onPlayerChangeLevel(player);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                EventInit.onPlayerJoin(player);
            }
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerRespawnEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                EventInit.onPlayerRespawn(player);
            }
        });
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, event -> {
            if (event.getPlayer() == null) {
                EventInit.onDataPackReload(event.getPlayerList().getServer());
            }
        });
    }

    private static void register(RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE, helper -> ItemInit.registerComponents(helper::register));
        event.register(Registries.MOB_EFFECT, helper -> EffectInit.register(helper::register));
        event.register(Registries.BLOCK, helper -> BlockInit.register(helper::register));
        event.register(Registries.ITEM, helper -> ItemInit.register(helper::register));
        event.register(Registries.POTION, helper -> ItemInit.registerPotions(helper::register));
        event.register(Registries.BLOCK_ENTITY_TYPE, helper -> BlockInit.registerBlockEntities(helper::register));
        event.register(Registries.MENU, helper -> BlockInit.registerMenus(helper::register));
        event.register(Registries.CREATIVE_MODE_TAB,
                helper -> helper.register(ItemInit.ENVIRONMENTZ_ITEM_GROUP.identifier(), ItemInit.createCreativeTab(CreativeModeTab.builder())));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(AffectionPacket.PACKET_ID, AffectionPacket.PACKET_CODEC);
        registrar.playToClient(TemperaturePacket.PACKET_ID, TemperaturePacket.PACKET_CODEC);
        registrar.playToClient(ThermometerPacket.PACKET_ID, ThermometerPacket.PACKET_CODEC);
        registrar.playToClient(SyncValuesPacket.PACKET_ID, SyncValuesPacket.PACKET_CODEC);
    }

}
