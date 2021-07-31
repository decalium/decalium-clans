package org.gepron1x.clans.storage;

import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.storage.dao.ClanDao;
import org.gepron1x.clans.storage.dao.ClanMemberDao;
import org.gepron1x.clans.storage.dao.StatisticDao;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClanLoader implements Loader<List<Clan>> {
    private final DecaliumClans plugin;

    public ClanLoader(DecaliumClans plugin) {
        this.plugin = plugin;
    }
    @Override
    public List<Clan> load(Jdbi jdbi) {
        Map<String, ClanBuilder> clans = jdbi.withExtension(ClanDao.class, dao -> {
            dao.createTable();
            return dao.getClans();
        }).stream().collect(Collectors.toMap(ClanBuilder::tag, Function.identity()));
        jdbi.withExtension(ClanMemberDao.class, dao -> {
            dao.createTable();
            return dao.loadMembers();
        }).forEach((key, value) -> {
            ClanBuilder clan = clans.get(value);
            if(clan == null) return;
            clan.addMember(key);
        });
        jdbi.withExtension(StatisticDao.class, dao -> {
            dao.createTable();
            return dao.getStats();
        }).forEach(row -> {
            ClanBuilder clan = clans.get(row.clanTag());
            if(clan != null) {
                clan.setStatistic(row.statType(), row.value());
            }
        });

        return clans.values().stream().map(ClanBuilder::build).collect(Collectors.toList());
    }
}
