package me.neovitalism.neoclear.managers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import me.neovitalism.neoclear.api.cleartypes.ClearTypeRegistry;
import me.neovitalism.neoclear.util.ServerUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {
    private static final ScheduledExecutorService SCHEDULER;
    static {
        ThreadFactory scheduledThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("NeoClear-Scheduler-Thread")
                .setDaemon(true)
                .build();
        SCHEDULER = Executors.newScheduledThreadPool(1, scheduledThreadFactory);
    }

    private static final Map<String, ClearType<?>> activeSchedules = new HashMap<>();

    public static void startTicking() {
        SCHEDULER.scheduleAtFixedRate(() ->
                ScheduleManager.activeSchedules.values().forEach(ClearType::tick),
                1, 1, TimeUnit.SECONDS);
    }

    public static void loadSchedules(Configuration config) {
        ScheduleManager.activeSchedules.clear();
        if(config == null) return;
        for(String key : config.getKeys()) {
            createSchedule(key, config.getSection(key));
        }
    }

    private static void createSchedule(String key, Configuration scheduleConfig) {
        String type = scheduleConfig.getString("type");
        Class<? extends ClearType<?>> clearTypeClass = ClearTypeRegistry.getClearType(type);
        if(clearTypeClass == null) {
            ServerUtil.log("&4" + type + " is not a valid clear type for schedule \"" + key + "\". Skipping enabling this schedule.");
            return;
        }
        try {
            ClearType<?> value = clearTypeClass.getConstructor(Configuration.class).newInstance(scheduleConfig);
            ScheduleManager.activeSchedules.put(key, value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            ServerUtil.log("&4Something went wrong enabling schedule \"" + key + "\". Skipping enabling this schedule.");
        }
    }

    public static void shutdown() {
        SCHEDULER.shutdownNow();
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
        for(ClearType<?> value : ScheduleManager.activeSchedules.values()) {
            value.doClear();
        }
    }
}
