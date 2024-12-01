package me.neovitalism.neoclear.api.cleartypes;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.utils.TimeUtil;
import me.neovitalism.neoclear.managers.ScheduleManager;
import me.neovitalism.neoclear.util.ServerUtil;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClearType<T> {
    private final long interval;
    private final Map<Long, String> intervalMessages = new HashMap<>();
    private final Map<Long, List<String>> intervalCommands = new HashMap<>();
    private final String clearMessage;
    private final List<String> clearCommands;
    protected List<String> whitelist;
    protected List<String> blacklistedWorlds;

    private long tickCount = 0;

    public ClearType(Configuration config) {
        this.interval = config.getLong("interval", -1);
        Configuration messageSection = config.getSection("messages");
        if (messageSection != null) {
            for (String key : messageSection.getKeys()) {
                if (key.equals("on-clear")) continue;
                long seconds = TimeUtil.parseSeconds(key);
                if (seconds == -1) continue;
                this.intervalMessages.put(seconds, messageSection.getString(key));
            }
            this.clearMessage = messageSection.getString("on-clear");
        } else this.clearMessage = null;
        Configuration commandSection = config.getSection("commands");
        if (commandSection != null) {
            for (String key : commandSection.getKeys()) {
                if (key.equals("on-clear")) continue;
                long seconds = TimeUtil.parseSeconds(key);
                if (seconds == -1) continue;
                this.intervalCommands.put(seconds, commandSection.getStringList(key));
            }
            this.clearCommands = commandSection.getStringList("on-clear");
        } else this.clearCommands = new ArrayList<>();
        this.whitelist = config.getStringList("whitelist");
        this.blacklistedWorlds = config.getStringList("blacklisted-worlds");
    }

    public abstract boolean isWhitelisted(T object);

    protected abstract long clear(List<ServerWorld> worlds);

    private List<ServerWorld> collectWorlds() {
        List<ServerWorld> worlds = new ArrayList<>();
        for (ServerWorld world : NeoAPI.getServer().getWorlds()) {
            if (!this.blacklistedWorlds.contains(world.getRegistryKey().getValue().toString())) worlds.add(world);
        }
        return worlds;
    }

    public void doClear() {
        long amountCleared = this.clear(this.collectWorlds());
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{interval}", String.valueOf(this.interval));
        replacements.put("{interval-formatted}", TimeUtil.getFormattedTime(this.interval));
        replacements.put("{amount}", String.valueOf(amountCleared));
        String clearMessage = this.clearMessage;
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            clearMessage = clearMessage.replace(replacement.getKey(), replacement.getValue());
        }
        ServerUtil.broadcastMessage(clearMessage);
        ServerUtil.executeCommands(this.clearCommands, replacements);
    }

    public void tick() {
        if (interval <= 0) return;
        this.tickCount++;
        if (this.tickCount == this.interval) {
            this.tickCount = 0;
            ScheduleManager.executeSync(this::doClear);
            return;
        }
        if (!this.intervalMessages.isEmpty()) {
            String message = this.intervalMessages.get(this.interval - this.tickCount);
            if (message != null) ServerUtil.broadcastMessage(message);
        }
        if (!this.intervalCommands.isEmpty()) {
            List<String> commands = this.intervalCommands.get(this.interval - this.tickCount);
            if (commands != null) ServerUtil.executeCommands(commands, null);
        }
    }
}
