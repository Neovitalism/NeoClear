package me.neovitalism.neoclear.builtin;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;

import java.util.ArrayList;
import java.util.List;

public class EntityClearType extends ClearType<Entity> {
    private static final List<String> IGNORED_ENTITY_TYPES = new ArrayList<>();
    static {
        IGNORED_ENTITY_TYPES.add("cobblemon:empty_pokeball");
        IGNORED_ENTITY_TYPES.add("cobblemon:pokemon");
        IGNORED_ENTITY_TYPES.add("cobblemon:npc");
        IGNORED_ENTITY_TYPES.add("cobblemon:generic_bedrock");
        IGNORED_ENTITY_TYPES.add("taterzens:npc");
    }

    public EntityClearType(Configuration config) {
        super(config);
    }

    @Override
    public boolean isWhitelisted(Entity entity) {
        Identifier entityID = Registries.ENTITY_TYPE.getId(entity.getType());
        String stringID = entityID.toString();
        if (EntityClearType.IGNORED_ENTITY_TYPES.contains(stringID)) return true;
        return this.whitelist.contains(stringID);
    }

    @Override
    public long clear(List<ServerWorld> worlds) {
        long clearCount = 0;
        for (ServerWorld world : worlds) {
            List<? extends MobEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), entity -> {
                if (entity == null) return false;
                if (!entity.isAlive()) return false;
                if (entity.isPersistent()) return false;
                if (entity.hasCustomName()) return false;
                return !this.isWhitelisted(entity);
            });
            for (LivingEntity entity : entities) {
                entity.discard();
                clearCount++;
            }
        }
        return clearCount;
    }
}
