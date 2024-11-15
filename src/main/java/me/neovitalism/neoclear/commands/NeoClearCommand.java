package me.neovitalism.neoclear.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.neovitalism.neoapi.modloading.command.ReloadCommand;
import me.neovitalism.neoapi.modloading.command.SuggestionProviders;
import me.neovitalism.neoapi.permissions.NeoPermission;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoclear.NeoClear;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import me.neovitalism.neoclear.managers.ScheduleManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NeoClearCommand extends ReloadCommand {
    public NeoClearCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        super(NeoClear.inst(), dispatcher, "neoclear");
    }

    @Override
    public NeoPermission[] getBasePermissions() {
        return NeoPermission.add(this.reloadPermission, "neoclear.clear");
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return command.then(literal("clear")
                .requires(NeoPermission.of("neoclear.clear")::matches)
                .then(argument("type", StringArgumentType.string())
                        .suggests((context, builder) -> new SuggestionProviders.List("type",
                                ScheduleManager.getAllNames()).getSuggestions(context, builder))
                        .executes(context -> {
                            String typeName = context.getArgument("type", String.class);
                            if (typeName.equals("all")) ScheduleManager.clearAll();
                            else {
                                ClearType<?> type = ScheduleManager.getSchedule(typeName);
                                if (type == null) {
                                    context.getSource().sendMessage(ColorUtil.parseColour(
                                            NeoClear.inst().getModPrefix() + "&c\"" + typeName +
                                                    "\" is not a valid schedule. Try using one from tab-complete."));
                                } else type.doClear();
                            }
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}
