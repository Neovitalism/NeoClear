package me.neovitalism.neoclear;

import me.neovitalism.neoapi.lang.LangManager;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoclear.commands.NeoClearCommand;
import me.neovitalism.neoclear.managers.ScheduleManager;
import me.neovitalism.neoclear.util.ServerUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class NeoClear extends NeoMod {
    private static NeoClear instance;

    public static NeoClear inst() {
        return instance;
    }

    @Override
    public String getModID() {
        return "NeoClear";
    }

    @Override
    public String getModPrefix() {
        return "&#696969[&#9632faN&#a240ece&#ae4ddfo&#ba5bd1C&#c768c4l&#d376b6e&#df83a9a&#eb919br&#696969]&f ";
    }

    @Override
    public LangManager getLangManager() {
        return null;
    }

    @Override
    public void onInitialize() {
        instance = this;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                new NeoClearCommand(this, dispatcher));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleManager.startTicking());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ScheduleManager.shutdown());
    }

    @Override
    public void configManager() {
        Configuration config = this.getDefaultConfig();
        ServerUtil.setPrefix(config.getString("prefix"));
        Configuration scheduleConfig = config.getSection("schedules");
        ScheduleManager.loadSchedules(scheduleConfig);
    }
}
