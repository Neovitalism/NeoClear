package me.neovitalism.neoclear.builtin;

import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoclear.NeoClear;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;

import java.util.List;

public class ItemClearType extends ClearType<ItemEntity> {
    public ItemClearType(Configuration config) {
        super(config);
    }

    @Override
    public boolean isWhitelisted(ItemEntity itemEntity) {
        Identifier itemID = Registries.ITEM.getId(itemEntity.getStack().getItem());
        String stringID = itemID.toString();
        return this.whitelist.contains(stringID);
    }

    @Override
    public long clear(List<ServerWorld> worlds) {
        long clearCount = 0;
        for (ServerWorld world : worlds) {
            List<? extends ItemEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), entity -> {
                if(entity == null) return false;
                if(!entity.isAlive()) return false;
                if(entity.hasCustomName()) return false;
                if(entity.getStack().getSubNbt("display") != null) return false;
                return !isWhitelisted(entity);
            });
            for(ItemEntity entity : entities) {
                entity.discard();
                clearCount++;
            }
        }
        return clearCount;
    }
}
