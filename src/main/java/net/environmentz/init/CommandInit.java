package net.environmentz.init;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.environmentz.api.EnvironmentzApi;
import net.environmentz.temperature.TemperatureManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("environment")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("info").requires(source -> source.hasPermissionLevel(3))
                        .then(infoCommand("affection", (player, manager) -> Text.translatable("commands.environment.affection", player.getDisplayName(), manager.isHotEnvAffected(),
                                manager.isColdEnvAffected())))
                        .then(infoCommand("resistance", (player, manager) -> Text.translatable("commands.environment.resistance", player.getDisplayName(), manager.getPlayerHeatResistance(),
                                manager.getPlayerColdResistance())))
                        .then(infoCommand("protection", (player, manager) -> Text.translatable("commands.environment.protection", player.getDisplayName(),
                                manager.getPlayerHeatProtectionAmount(), manager.getPlayerColdProtectionAmount())))
                        .then(infoCommand("temperature", (player, manager) -> Text.translatable("commands.environment.temperature", player.getDisplayName(), manager.getPlayerTemperature()))))
                .then(CommandManager.literal("affection").then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("hot").then(CommandManager.argument("affection", BoolArgumentType.bool())
                                .executes(context -> setAffection(context, true))))
                        .then(CommandManager.literal("cold").then(CommandManager.argument("affection", BoolArgumentType.bool())
                                .executes(context -> setAffection(context, false))))))
                .then(CommandManager.literal("resistance").then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("hot").then(CommandManager.argument("resistance", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "resistance", "commands.environment.resistance_changed",
                                        (manager, value) -> manager.setPlayerHeatResistance(value)))))
                        .then(CommandManager.literal("cold").then(CommandManager.argument("resistance", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "resistance", "commands.environment.resistance_changed",
                                        (manager, value) -> manager.setPlayerColdResistance(value)))))))
                .then(CommandManager.literal("protection").then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("hot").then(CommandManager.argument("protection", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "protection", "commands.environment.protection_changed",
                                        (manager, value) -> manager.setPlayerHeatProtectionAmount(value)))))
                        .then(CommandManager.literal("cold").then(CommandManager.argument("protection", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "protection", "commands.environment.protection_changed",
                                        (manager, value) -> manager.setPlayerColdProtectionAmount(value)))))))
                .then(CommandManager.literal("temperature").then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.argument("temperature", IntegerArgumentType.integer()).executes(CommandInit::setTemperature))))));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> infoCommand(String name, InfoFormatter formatter) {
        return CommandManager.literal(name).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(context -> {
            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
            for (ServerPlayerEntity player : targets) {
                Text message = formatter.format(player, EnvironmentzApi.getTemperatureManager(player));
                context.getSource().sendFeedback(() -> message, true);
            }
            return targets.size();
        }));
    }

    private static int setAffection(CommandContext<ServerCommandSource> context, boolean hot) throws CommandSyntaxException {
        boolean affection = BoolArgumentType.getBool(context, "affection");
        return forEachTarget(context, "commands.environment.affection_changed", player -> {
            TemperatureManager manager = EnvironmentzApi.getTemperatureManager(player);
            EnvironmentzApi.setEnvironmentAffection(player, hot ? affection : manager.isHotEnvAffected(), hot ? manager.isColdEnvAffected() : affection);
        });
    }

    private static int setValue(CommandContext<ServerCommandSource> context, String argument, String feedbackKey, BiConsumer<TemperatureManager, Integer> setter) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, argument);
        return forEachTarget(context, feedbackKey, player -> setter.accept(EnvironmentzApi.getTemperatureManager(player), value));
    }

    private static int setTemperature(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int temperature = IntegerArgumentType.getInteger(context, "temperature");
        return forEachTarget(context, "commands.environment.temperature_changed", player -> EnvironmentzApi.setTemperature(player, temperature));
    }

    private static int forEachTarget(CommandContext<ServerCommandSource> context, String feedbackKey, Consumer<ServerPlayerEntity> action) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
        for (ServerPlayerEntity player : targets) {
            action.accept(player);
            context.getSource().sendFeedback(() -> Text.translatable(feedbackKey, player.getDisplayName()), true);
        }
        return targets.size();
    }

    @FunctionalInterface
    private interface InfoFormatter {
        Text format(ServerPlayerEntity player, TemperatureManager manager);
    }
}
