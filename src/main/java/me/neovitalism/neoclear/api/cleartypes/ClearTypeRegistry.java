package me.neovitalism.neoclear.api.cleartypes;

import me.neovitalism.neoclear.builtin.CobblemonClearType;
import me.neovitalism.neoclear.builtin.EntityClearType;
import me.neovitalism.neoclear.builtin.ItemClearType;

import java.util.HashMap;
import java.util.Map;

public class ClearTypeRegistry {
    public static final Map<String, Class<? extends ClearType<?>>> clearTypes = new HashMap<>();

    public static void register(String name, Class<? extends ClearType<?>> clazz) {
        ClearTypeRegistry.clearTypes.put(name, clazz);
    }

    public static Class<? extends ClearType<?>> getClearType(String name) {
        if(name == null) return null;
        return clearTypes.get(name);
    }

    static {
        ClearTypeRegistry.register("COBBLEMON", CobblemonClearType.class);
        ClearTypeRegistry.register("ENTITY", EntityClearType.class);
        ClearTypeRegistry.register("ITEM", ItemClearType.class);
    }
}
