package org.gepron1x.clans.plugin.storage.implementation.sql.common;

import org.bukkit.Location;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class SavableHomes implements Savable {

    private static final String INSERT_HOME = "INSERT INTO homes (clan_id, name, display_name, creator, icon) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_LOCATION = "INSERT INTO `locations` (`home_id`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)";

    private final int clanId;
    private final Collection<? extends ClanHome> homes;

    public SavableHomes(int clanId, Collection<? extends ClanHome> homes) {

        this.clanId = clanId;
        this.homes = homes;
    }

    public SavableHomes(Handle handle, int clanId, ClanHome home) {
        this(clanId, Collections.singletonList(home));
    }
    @Override
    public int execute(Handle handle) {
        PreparedBatch batch = handle.prepareBatch(INSERT_HOME);
        List<ClanHome> homeList = List.copyOf(homes);
        for(ClanHome home : homeList) {
            batch.add(this.clanId, home.name(), home.displayName(), home.creator(), home.icon());
        }

        PreparedBatch locations = handle.prepareBatch(INSERT_LOCATION);
        List<Integer> ids = batch.executeAndReturnGeneratedKeys("id").mapTo(Integer.class).list();
        if(homeList.size() != ids.size()) {
            handle.rollback();
            throw new IllegalStateException("Homes with some names already exists for this clan.");
        }

        for(int i = 0; i < ids.size(); i++) {
            ClanHome home = homeList.get(i);
            int id = ids.get(i);
            Location location = home.location();
            locations.add(id, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
        }

        return Arrays.stream(locations.execute()).sum();
    }
}
