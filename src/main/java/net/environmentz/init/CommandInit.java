package net.environmentz.init;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.environmentz.api.EnvironmentzApi;
import net.environmentz.temperature.TemperatureManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("environment")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("info").requires(source -> source.hasPermission(3))
                        .then(infoCommand("affection", (player, manager) -> Component.translatable("commands.environment.affection", player.getDisplayName(), manager.isHotEnvAffected(),
                                manager.isColdEnvAffected())))
                        .then(infoCommand("resistance", (player, manager) -> Component.translatable("commands.environment.resistance", player.getDisplayName(), manager.getPlayerHeatResistance(),
                                manager.getPlayerColdResistance())))
                        .then(infoCommand("protection", (player, manager) -> Component.translatable("commands.environment.protection", player.getDisplayName(),
                                manager.getPlayerHeatProtectionAmount(), manager.getPlayerColdProtectionAmount())))
                        .then(infoCommand("temperature", (player, manager) -> Component.translatable("commands.environment.temperature", player.getDisplayName(), manager.getPlayerTemperature()))))
                .then(Commands.literal("affection").then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("hot").then(Commands.argument("affection", BoolArgumentType.bool())
                                .executes(context -> setAffection(context, true))))
                        .then(Commands.literal("cold").then(Commands.argument("affection", BoolArgumentType.bool())
                                .executes(context -> setAffection(context, false))))))
                .then(Commands.literal("resistance").then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("hot").then(Commands.argument("resistance", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "resistance", "commands.environment.resistance_changed",
                                        (manager, value) -> manager.setPlayerHeatResistance(value)))))
                        .then(Commands.literal("cold").then(Commands.argument("resistance", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "resistance", "commands.environment.resistance_changed",
                                        (manager, value) -> manager.setPlayerColdResistance(value)))))))
                .then(Commands.literal("protection").then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.literal("hot").then(Commands.argument("protection", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "protection", "commands.environment.protection_changed",
                                        (manager, value) -> manager.setPlayerHeatProtectionAmount(value)))))
                        .then(Commands.literal("cold").then(Commands.argument("protection", IntegerArgumentType.integer())
                                .executes(context -> setValue(context, "protection", "commands.environment.protection_changed",
                                        (manager, value) -> manager.setPlayerColdProtectionAmount(value)))))))
                .then(Commands.literal("temperature").then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("temperature", IntegerArgumentType.integer()).executes(CommandInit::setTemperature))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> infoCommand(String name, InfoFormatter formatter) {
        return Commands.literal(name).then(Commands.argument("targets", EntityArgument.players()).executes(context -> {
            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
            for (ServerPlayer player : targets) {
                Component message = formatter.format(player, EnvironmentzApi.getTemperatureManager(player));
                context.getSource().sendSuccess(() -> message, true);
            }
            return targets.size();
        }));
    }

    private static int setAffection(CommandContext<CommandSourceStack> context, boolean hot) throws CommandSyntaxException {
        boolean affection = BoolArgumentType.getBool(context, "affection");
        return forEachTarget(context, "commands.environment.affection_changed", player -> {
            TemperatureManager manager = EnvironmentzApi.getTemperatureManager(player);
            EnvironmentzApi.setEnvironmentAffection(player, hot ? affection : manager.isHotEnvAffected(), hot ? manager.isColdEnvAffected() : affection);
        });
    }

    private static int setValue(CommandContext<CommandSourceStack> context, String argument, String feedbackKey, BiConsumer<TemperatureManager, Integer> setter) throws CommandSyntaxException {
        int value = IntegerArgumentType.getInteger(context, argument);
        return forEachTarget(context, feedbackKey, player -> setter.accept(EnvironmentzApi.getTemperatureManager(player), value));
    }

    private static int setTemperature(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int temperature = IntegerArgumentType.getInteger(context, "temperature");
        return forEachTarget(context, "commands.environment.temperature_changed", player -> EnvironmentzApi.setTemperature(player, temperature));
    }

    private static int forEachTarget(CommandContext<CommandSourceStack> context, String feedbackKey, Consumer<ServerPlayer> action) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        for (ServerPlayer player : targets) {
            action.accept(player);
            context.getSource().sendSuccess(() -> Component.translatable(feedbackKey, player.getDisplayName()), true);
        }
        return targets.size();
    }

    @FunctionalInterface
    private interface InfoFormatter {
        Component format(ServerPlayer player, TemperatureManager manager);
    }
}
