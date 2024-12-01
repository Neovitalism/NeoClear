package me.neovitalism.neoclear.managers;

import me.neovitalism.neoapi.async.NeoAPIExecutorManager;
import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoclear.NeoClear;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import me.neovitalism.neoclear.api.cleartypes.ClearTypeRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {
    private static final NeoExecutor EXECUTOR = NeoAPIExecutorManager.createScheduler(
            "NeoClear-Scheduler-Thread", 1);
    private static ScheduledFuture<?> TICK_FUTURE;
    private static final Map<String, ClearType<?>> activeSchedules = new HashMap<>();

    public static void startTicking() {
        ScheduleManager.TICK_FUTURE = ScheduleManager.EXECUTOR.scheduleRepeatingTaskAsync(() ->
                ScheduleManager.activeSchedules.values().forEach(ClearType::tick), 1, 1, TimeUnit.SECONDS);
    }

    public static void loadSchedules(Configuration config) {
        ScheduleManager.activeSchedules.clear();
        if (config == null) return;
        for (String key : config.getKeys()) ScheduleManager.createSchedule(key, config.getSection(key));
    }

    private static void createSchedule(String key, Configuration scheduleConfig) {
        String type = scheduleConfig.getString("type");
        Class<? extends ClearType<?>> clazz = ClearTypeRegistry.getClearType(type);
        if (clazz == null) {
            NeoClear.inst().getLogger().error("&4" + type + " is not a valid clear type for schedule \"" +
                    key + "\". " + "Skipping enabling this schedule.");
            return;
        }
        try {
            ClearType<?> value = clazz.getConstructor(Configuration.class).newInstance(scheduleConfig);
            ScheduleManager.activeSchedules.put(key, value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            NeoClear.inst().getLogger().error("&4Something went wrong enabling schedule \"" +
                    key + "\". Skipping enabling this schedule.");
        }
    }

    public static List<String> getAllNames() {
        List<String> allNames = new ArrayList<>();
        allNames.add("all");
        allNames.addAll(ScheduleManager.activeSchedules.keySet());
        return allNames;
    }

    public static ClearType<?> getSchedule(String name) {
        return ScheduleManager.activeSchedules.get(name);
    }

    public static void clearAll() {
        for (ClearType<?> value : ScheduleManager.activeSchedules.values()) value.doClear();
    }

    public static void executeSync(Runnable runnable) {
        ScheduleManager.EXECUTOR.runTaskSync(runnable);
    }

    public static void shutdown() {
        ScheduleManager.TICK_FUTURE.cancel(true);
    }
}
