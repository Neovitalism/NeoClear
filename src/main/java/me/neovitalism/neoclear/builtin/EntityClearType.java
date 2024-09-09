package me.neovitalism.neoclear.builtin;

import me.neovitalism.neoapi.modloading.config.Configuration;
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
    private static final List<String> defaultWhitelist = new ArrayList<>();
    static {
        defaultWhitelist.add("cobblemon:empty_pokeball");
        defaultWhitelist.add("cobblemon:pokemon");
        defaultWhitelist.add("taterzens:npc");
    }

    public EntityClearType(Configuration config) {
        super(config);
    }

    @Override
    public boolean isWhitelisted(Entity entity) {
        Identifier entityID = Registries.ENTITY_TYPE.getId(entity.getType());
        String stringID = entityID.toString();
        if(EntityClearType.defaultWhitelist.contains(stringID)) return true;
        return this.whitelist.contains(stringID);
    }

    @Override
    public long clear(List<ServerWorld> worlds) {
        long clearCount = 0;
        for (ServerWorld world : worlds) {
            List<? extends MobEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(MobEntity.class), entity -> {
                if(entity == null) return false;
                if(!entity.isAlive()) return false;
                if(entity.isPersistent()) return false;
                if(entity.hasCustomName()) return false;
                return !isWhitelisted(entity);
            });
            for(LivingEntity entity : entities) {
                entity.discard();
                clearCount++;
            }
        }
        return clearCount;
    }
}
