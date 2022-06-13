package net.environmentz.init;

import net.environmentz.access.PlayerEnvAccess;
import net.environmentz.network.EnvironmentServerPacket;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.brigadier.arguments.BoolArgumentType;

public class CommandInit {

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("environment").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(3);
            })).then(CommandManager.argument("targets", EntityArgumentType.players())
                    .then(CommandManager.literal("hot").then(CommandManager.argument("affected", BoolArgumentType.bool()).executes((commandContext) -> {
                        return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "hot", BoolArgumentType.getBool(commandContext, "affected"));
                    }))).then(CommandManager.literal("cold").then(CommandManager.argument("affected", BoolArgumentType.bool()).executes((commandContext) -> {
                        return executeEnvCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), "cold", BoolArgumentType.getBool(commandContext, "affected"));
                    })))));
        });
    }

    private static int executeEnvCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String environment, boolean affected) {
        Iterator<ServerPlayerEntity> var3 = targets.iterator();

        while (var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var3.next();
            if (environment.equals("hot"))
                ((PlayerEnvAccess) serverPlayerEntity).setHotEnvAffected(affected);
            if (environment.equals("cold"))
                ((PlayerEnvAccess) serverPlayerEntity).setColdEnvAffected(affected);
            EnvironmentServerPacket.writeS2CSyncEnvPacket(serverPlayerEntity, ((PlayerEnvAccess) serverPlayerEntity).isHotEnvAffected(), ((PlayerEnvAccess) serverPlayerEntity).isColdEnvAffected());
        }
        source.sendFeedback(Text.translatable("commands.environment.changed"), true);

        return targets.size();
    }

}