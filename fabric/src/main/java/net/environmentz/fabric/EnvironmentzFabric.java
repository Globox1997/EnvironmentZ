package net.environmentz.fabric;

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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;

public class EnvironmentzFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        EnvironmentzMain.init();

        ItemInit.registerComponents((id, component) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, component));
        EffectInit.register((id, effect) -> Registry.register(BuiltInRegistries.MOB_EFFECT, id, effect));
        BlockInit.register((id, block) -> Registry.register(BuiltInRegistries.BLOCK, id, block));
        ItemInit.register((id, item) -> Registry.register(BuiltInRegistries.ITEM, id, item));
        ItemInit.registerPotions((id, potion) -> Registry.register(BuiltInRegistries.POTION, id, potion));
        BlockInit.registerBlockEntities((id, blockEntityType) -> Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, blockEntityType));
        BlockInit.registerMenus((id, menuType) -> Registry.register(BuiltInRegistries.MENU, id, menuType));
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ItemInit.ENVIRONMENTZ_ITEM_GROUP, ItemInit.createCreativeTab(FabricCreativeModeTab.builder()));
        CreativeModeTabEvents.modifyOutputEvent(ItemInit.ENVIRONMENTZ_ITEM_GROUP).register(output -> ItemInit.getCreativeTabItems().forEach(output::accept));

        PayloadTypeRegistry.clientboundPlay().register(AffectionPacket.PACKET_ID, AffectionPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TemperaturePacket.PACKET_ID, TemperaturePacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ThermometerPacket.PACKET_ID, ThermometerPacket.PACKET_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncValuesPacket.PACKET_ID, SyncValuesPacket.PACKET_CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandInit.register(dispatcher));
        LoaderInit.register((id, listener) -> ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id, listener));
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> LootInit.modifyLootTable(key, tableBuilder::pool));

        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> EventInit.onPlayerChangeLevel(player));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> EventInit.onPlayerJoin(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> EventInit.onPlayerRespawn(newPlayer));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                EventInit.onDataPackReload(server);
            }
        });
    }

}
