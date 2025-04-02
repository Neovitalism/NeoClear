package me.neovitalism.neoclear.util;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoapi.utils.CommandUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.neoclear.managers.ScheduleManager;

import java.util.List;
import java.util.Map;

public class ServerUtil {
    private static String prefix = null;

    public static void setPrefix(String prefix) {
        ServerUtil.prefix = prefix;
    }

    public static void broadcastMessage(String message) {
        NeoAPI.adventure().all().sendMessage(ColorUtil.parseColour(ServerUtil.prefix + message));
    }

    public static void executeCommands(List<String> commands, Map<String, String> replacements) {
        ScheduleManager.executeSync(() -> {
            for (String command : commands) CommandUtil.executeServerCommand(StringUtil.replaceReplacements(command, replacements));
        });
    }
}
