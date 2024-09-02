package me.neovitalism.neoclear.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.CommandBase;
import me.neovitalism.neoapi.modloading.command.ListSuggestionProvider;
import me.neovitalism.neoapi.utils.ChatUtil;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import me.neovitalism.neoclear.managers.ScheduleManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NeoClearCommand implements CommandBase {
    public NeoClearCommand(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        this.register(instance, dispatcher);
    }

    @Override
    public String[] getCommandAliases() {
        return new String[0];
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> register(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        return dispatcher.register(literal("neoclear")
                .requires(serverCommandSource ->
                        NeoMod.checkForPermission(serverCommandSource, "neoclear.reload", 4) ||
                                NeoMod.checkForPermission(serverCommandSource, "neoclear.clear", 4))
                .then(literal("reload")
                        .requires(serverCommandSource ->
                                NeoMod.checkForPermission(serverCommandSource, "neoclear.reload", 4))
                        .executes(context -> {
                            instance.configManager();
                            ChatUtil.sendPrettyMessage(context.getSource(), instance.getModPrefix(), "&aReloaded Config!");
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(literal("clear")
                        .requires(serverCommandSource ->
                                NeoMod.checkForPermission(serverCommandSource, "neoclear.clear", 4))
                        .then(argument("type", StringArgumentType.string())
                                .suggests((context, builder) ->
                                        new ListSuggestionProvider("type", ScheduleManager.getAllNames())
                                                .getSuggestions(context, builder))
                                .executes(context -> {
                                    String typeName = context.getArgument("type", String.class);
                                    if(typeName.equals("all")) {
                                        ScheduleManager.clearAll();
                                    } else {
                                        ClearType<?> type = ScheduleManager.getSchedule(typeName);
                                        if(type == null) {
                                            ChatUtil.sendPrettyMessage(context.getSource(), instance.getModPrefix(),
                                                    "&c\"" + typeName + "\" is not a valid schedule. Try using one from tab-complete.");
                                        } else type.doClear();
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))));
    }
}
