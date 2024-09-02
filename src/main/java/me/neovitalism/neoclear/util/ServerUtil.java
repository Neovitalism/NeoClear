package me.neovitalism.neoclear.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoclear.NeoClear;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;

public class ServerUtil {
    private static String prefix = null;

    public static void setPrefix(String prefix) {
        ServerUtil.prefix = prefix;
    }

    public static void sendMessage(String message) {
        NeoClear.inst().adventure().all().sendMessage(ColorUtil.parseColour(prefix + message));
    }

    public static void log(String message) {
        NeoClear.inst().adventure().console().sendMessage(ColorUtil.parseColour(prefix + message));
    }

    public static void executeCommands(List<String> commands, Map<String, String> replacements) {
        for(String command : commands) {
            String toExecute = command;
            if(replacements != null) {
                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    toExecute = toExecute.replace(replacement.getKey(), replacement.getValue());
                }
            }
            MinecraftServer server = NeoClear.inst().getServer();
            try {
                server.getCommandFunctionManager().getDispatcher().execute(toExecute, server.getCommandSource());
            } catch (CommandSyntaxException ignored) {}
        }
    }
}
