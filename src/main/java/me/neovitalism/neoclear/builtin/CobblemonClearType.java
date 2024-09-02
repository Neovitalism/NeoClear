package me.neovitalism.neoclear.builtin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoclear.NeoClear;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;

import java.util.List;

public class CobblemonClearType extends ClearType<PokemonEntity> {
    public CobblemonClearType(Configuration config) {
        super(config);
    }

    @Override
    public boolean isWhitelisted(PokemonEntity pokemon) {
        for(String whitelisted : this.whitelist) {
            if(whitelisted.contains("tag:")) {
                String tag = whitelisted.replace("tag:", "");
                if(pokemon.getCommandTags().contains(tag)) return true;
            } else if(PokemonProperties.Companion.parse(whitelisted, " ", "=").matches(pokemon)) return true;
        }
        return false;
    }

    @Override
    public long clear(List<ServerWorld> worlds) {
        long clearCount = 0;
        for (ServerWorld world : worlds) {
            List<? extends PokemonEntity> entities = world.getEntitiesByType(
                    TypeFilter.instanceOf(PokemonEntity.class), pokemon -> true);
            for(PokemonEntity pokemon : entities) {
                if(pokemon == null) continue;
                if(!pokemon.isAlive()) continue;
                if(pokemon.isPersistent()) continue;
                if(pokemon.isBusy()) continue;
                if(pokemon.getOwnerUuid() != null) continue;
                if(pokemon.getTethering() != null) continue;
                if(isWhitelisted(pokemon)) continue;
                pokemon.setQueuedToDespawn(true);
                clearCount++;
            }
        }
        return clearCount;
    }
}
