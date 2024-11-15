package me.neovitalism.neoclear;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.CommandRegistryInfo;
import me.neovitalism.neoclear.api.cleartypes.ClearTypeRegistry;
import me.neovitalism.neoclear.commands.NeoClearCommand;
import me.neovitalism.neoclear.managers.ScheduleManager;
import me.neovitalism.neoclear.util.ServerUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class NeoClear extends NeoMod {
    private static NeoClear instance;

    @Override
    public String getModID() {
        return "NeoClear";
    }

    @Override
    public String getModPrefix() {
        return "&#696969[&#9632faN&#a240ece&#ae4ddfo&#ba5bd1C&#c768c4l&#d376b6e&#df83a9a&#eb919br&#696969]&f ";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        NeoClear.instance = this;
        ClearTypeRegistry.registerDefaults(this);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ScheduleManager.startTicking());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ScheduleManager.shutdown());
        this.getLogger().info("Loaded!");
    }

    @Override
    public void configManager() {
        Configuration config = this.getConfig("config.yml", true);
        ServerUtil.setPrefix(config.getString("prefix"));
        Configuration scheduleConfig = config.getSection("schedules");
        ScheduleManager.loadSchedules(scheduleConfig);
    }

    @Override
    public void registerCommands(CommandRegistryInfo info) {
        new NeoClearCommand(info.getDispatcher());
    }

    public static NeoClear inst() {
        return NeoClear.instance;
    }
}
