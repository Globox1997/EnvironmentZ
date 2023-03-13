package net.environmentz.init;

import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.environmentz.temperature.TemperatureManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("info").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.literal("affection").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeInfo(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 0);
            }))).then(CommandManager.literal("resistance").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeInfo(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 1);
            }))).then(CommandManager.literal("protection").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeInfo(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 2);
            }))).then(CommandManager.literal("temperature").then(CommandManager.argument("targets", EntityArgumentType.players()).executes((commandContext) -> {
                return executeInfo(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), 3);
            }))));
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("environment").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(2);
            })).then(CommandManager.literal("affection").then(CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.literal("hot").then(CommandManager.argument("affection", BoolArgumentType.bool()).executes((commandContext) -> {
                        return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "hot", BoolArgumentType.getBool(commandContext, "affection"),
                                0);
                    }))).then(CommandManager.literal("cold").then(CommandManager.argument("affection", BoolArgumentType.bool()).executes((commandContext) -> {
                        return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "cold", BoolArgumentType.getBool(commandContext, "affection"),
                                0);
                    }))))).then(CommandManager.literal("resistance").then(CommandManager.argument("targets", EntityArgumentType.players())
                            .then(CommandManager.literal("hot").then(CommandManager.argument("resistance", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "hot",
                                        IntegerArgumentType.getInteger(commandContext, "resistance"), 1);
                            }))).then(CommandManager.literal("cold").then(CommandManager.argument("resistance", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "cold",
                                        IntegerArgumentType.getInteger(commandContext, "resistance"), 1);
                            })))))
                    .then(CommandManager.literal("protection").then(CommandManager.argument("targets", EntityArgumentType.players())
                            .then(CommandManager.literal("hot").then(CommandManager.argument("protection", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "hot",
                                        IntegerArgumentType.getInteger(commandContext, "protection"), 2);
                            }))).then(CommandManager.literal("cold").then(CommandManager.argument("protection", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "cold",
                                        IntegerArgumentType.getInteger(commandContext, "protection"), 2);
                            })))))
                    .then(CommandManager.literal("temperature").then(
                            CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("temperature", IntegerArgumentType.integer()).executes((commandContext) -> {
                                return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "",
                                        IntegerArgumentType.getInteger(commandContext, "temperature"), 3);
                            })))));
        });
    }

    // 0: affection; 1: resistance; 2: protection; 3: temperature
    private static int executeEnvCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String environment, Object object, int mode) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();

        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();
            TemperatureManager temperatureManager = ((TemperatureManagerAccess) serverPlayerEntity).getTemperatureManager();

            if (mode == 0) {
                if (environment.equals("hot")) {
                    temperatureManager.setEnvironmentAffection((boolean) object, temperatureManager.isColdEnvAffected());
                } else if (environment.equals("cold")) {
                    temperatureManager.setEnvironmentAffection(temperatureManager.isHotEnvAffected(), (boolean) object);
                }
                EnvironmentServerPacket.writeS2CSyncEnvPacket(serverPlayerEntity, temperatureManager.isHotEnvAffected(), temperatureManager.isColdEnvAffected());
                source.sendFeedback(Text.translatable("commands.environment.affection_changed", serverPlayerEntity.getDisplayName()), true);
            } else if (mode == 1) {
                if (environment.equals("hot"))
                    temperatureManager.setPlayerHeatResistance((int) object);
                else if (environment.equals("cold"))
                    temperatureManager.setPlayerColdResistance((int) object);
                source.sendFeedback(Text.translatable("commands.environment.resistance_changed", serverPlayerEntity.getDisplayName()), true);
            } else if (mode == 2) {
                if (environment.equals("hot"))
                    temperatureManager.setPlayerHeatProtectionAmount((int) object);
                else if (environment.equals("cold"))
                    temperatureManager.setPlayerColdProtectionAmount((int) object);
                source.sendFeedback(Text.translatable("commands.environment.protection_changed", serverPlayerEntity.getDisplayName()), true);
            } else if (mode == 3) {
                temperatureManager.setPlayerTemperature((int) object);
                source.sendFeedback(Text.translatable("commands.environment.temperature_changed", serverPlayerEntity.getDisplayName()), true);
                EnvironmentServerPacket.writeS2CTemperaturePacket(serverPlayerEntity, temperatureManager.getPlayerTemperature(), temperatureManager.getPlayerWetIntensityValue());
            }
        }

        return targets.size();
    }

    // 0: affection; 1: resistance; 2: protection; 3: temperature
    private static int executeInfo(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int info) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();
        // loop over players
        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = var3.next();
            TemperatureManager temperatureManager = ((TemperatureManagerAccess) serverPlayerEntity).getTemperatureManager();

            if (info == 0)
                source.sendFeedback(
                        Text.translatable("commands.environment.affection", serverPlayerEntity.getDisplayName(), temperatureManager.isHotEnvAffected(), temperatureManager.isColdEnvAffected()), true);
            else if (info == 1)
                source.sendFeedback(Text.translatable("commands.environment.resistance", serverPlayerEntity.getDisplayName(), temperatureManager.getPlayerHeatResistance(),
                        temperatureManager.getPlayerColdResistance()), true);
            else if (info == 2)
                source.sendFeedback(Text.translatable("commands.environment.protection", serverPlayerEntity.getDisplayName(), temperatureManager.getPlayerHeatProtectionAmount(),
                        temperatureManager.getPlayerColdProtectionAmount()), true);
            else if (info == 3)
                source.sendFeedback(Text.translatable("commands.environment.temperature", serverPlayerEntity.getDisplayName(), temperatureManager.getPlayerTemperature()), true);

        }
        return 1;
    }

}