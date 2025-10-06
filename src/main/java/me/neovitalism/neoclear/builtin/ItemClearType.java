package me.neovitalism.neoclear.builtin;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.helpers.ItemHelper;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ItemEntity;
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
        Identifier identifier = ItemHelper.getIdentifier(itemEntity.getStack().getItem());
        if (this.whitelist.contains(identifier.getNamespace() + ":*")) return true;
        return this.whitelist.contains(identifier.toString());
    }

    @Override
    public long clear(List<ServerWorld> worlds) {
        long clearCount = 0;
        for (ServerWorld world : worlds) {
            List<? extends ItemEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), entity -> {
                if (entity == null) return false;
                if (!entity.isAlive()) return false;
                if (entity.getItemAge() == -32768) return false;
                if (entity.hasCustomName()) return false;
                if (entity.getStack().get(DataComponentTypes.ITEM_NAME) != null) return false;
                return !this.isWhitelisted(entity);
            });
            for (ItemEntity entity : entities) {
                entity.discard();
                clearCount++;
            }
        }
        return clearCount;
    }
}
