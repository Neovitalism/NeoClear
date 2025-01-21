package me.neovitalism.neoclear.builtin;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoclear.api.cleartypes.ClearType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;

import java.util.ArrayList;
import java.util.List;

public class CobblemonClearType extends ClearType<PokemonEntity> {
    private final List<String> whitelistedTags = new ArrayList<>();
    private final List<PokemonProperties> whitelistedSpecs = new ArrayList<>();

    public CobblemonClearType(Configuration config) {
        super(config);
        for (String whitelistEntry : this.whitelist) {
            if (whitelistEntry.contains("tag:")) {
                String tag = whitelistEntry.replace("tag:", "");
                this.whitelistedTags.add(tag);
            } else {
                PokemonProperties properties = PokemonProperties.Companion.parse(whitelistEntry, " ", "=");
                this.whitelistedSpecs.add(properties);
            }
        }
    }

    @Override
    public boolean isWhitelisted(PokemonEntity pokemon) {
        for (String whitelistedTag : this.whitelistedTags) {
            if (pokemon.getCommandTags().contains(whitelistedTag)) return true;
        }
        for (PokemonProperties spec : this.whitelistedSpecs) if (spec.matches(pokemon)) return true;
        return false;
    }

    @Override
    public long clear(List<ServerWorld> worlds) {
        long clearCount = 0;
        for (ServerWorld world : worlds) {
            List<? extends PokemonEntity> entities = world.getEntitiesByType(
                    TypeFilter.instanceOf(PokemonEntity.class), pokemon -> true);
            for (PokemonEntity pokemon : entities) {
                if (pokemon == null) continue;
                if (!pokemon.isAlive()) continue;
                if (pokemon.isPersistent()) continue;
                if (pokemon.isBusy()) continue;
                if (pokemon.getOwnerUuid() != null) continue;
                if (pokemon.getTethering() != null) continue;
                if (pokemon.getVehicle() != null) continue;
                if (pokemon.isLeashed()) continue;
                if (this.isWhitelisted(pokemon)) continue;
                pokemon.setQueuedToDespawn(true);
                clearCount++;
            }
        }
        return clearCount;
    }
}
