package org.gepron1x.clans.storage;

import org.gepron1x.clans.clan.Clan;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClanLoader implements Loader<List<Clan>> {
    @Override
    public List<Clan> load(Jdbi jdbi) {
        Map<String, Clan> clans = jdbi.withExtension(ClanDao.class, dao -> {
            dao.createTable();
            return dao.loadClans();
        }).stream().collect(Collectors.toMap(Clan::getTag, c -> c));
        jdbi.withExtension(ClanMemberDao.class, dao -> {
            dao.createTable();
            return dao.loadMembers();
        }).forEach((key, value) -> {
            Clan clan = clans.get(key);
            if(clan != null) clan.addMember(value);
        });
        jdbi.withExtension(StatisticDao.class, dao -> {
            dao.createTable();
            return dao.getStats();
        }).forEach(row -> {
            Clan clan = clans.get(row.clanTag());
            if(clan != null) clan.getStatistics().setValue(row.statType(), row.value());
        });
        return new ArrayList<>(clans.values());
    }
}
