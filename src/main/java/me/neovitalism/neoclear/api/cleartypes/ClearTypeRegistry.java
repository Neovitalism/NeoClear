package me.neovitalism.neoclear.api.cleartypes;

import me.neovitalism.neoapi.utils.ServerUtil;
import me.neovitalism.neoclear.NeoClear;
import me.neovitalism.neoclear.builtin.CobblemonClearType;
import me.neovitalism.neoclear.builtin.EntityClearType;
import me.neovitalism.neoclear.builtin.ItemClearType;

import java.util.HashMap;
import java.util.Map;

public class ClearTypeRegistry {
    public static final Map<String, Class<? extends ClearType<?>>> CLEAR_TYPES = new HashMap<>();

    public static void register(String name, Class<? extends ClearType<?>> clazz) {
        ClearTypeRegistry.CLEAR_TYPES.put(name, clazz);
    }

    public static Class<? extends ClearType<?>> getClearType(String name) {
        if (name == null) return null;
        return ClearTypeRegistry.CLEAR_TYPES.get(name);
    }

    public static void registerDefaults(NeoClear instance) {
        if (ServerUtil.isModLoaded("cobblemon")) {
            ClearTypeRegistry.register("COBBLEMON", CobblemonClearType.class);
            instance.getLogger().info("Hooked into Cobblemon!");
        }
        ClearTypeRegistry.register("ENTITY", EntityClearType.class);
        ClearTypeRegistry.register("ITEM", ItemClearType.class);
    }
}
