package org.gepron1x.clans.storage.task;

import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.helper.ClanHelper;
import org.gepron1x.clans.storage.ClanDao;
import org.bukkit.scheduler.BukkitRunnable;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataSyncTask extends BukkitRunnable {
    private static final List<String> CLAN_MEMBER_METHODS = List.of("member.getUniqueId",
            "member.getName",
            "clan.getTag",
            "member.getRole");
    private final ClanHelper helper;
    private final Jdbi jdbi;

    public DataSyncTask(ClanHelper helper, Jdbi jdbi) {
        this.helper = helper;
        this.jdbi = jdbi;
    }
    private final record MemberEntry(Clan clan, ClanMember member) {}

    @Override
    public void run() {
        Collection<Clan> clans = helper.getClans();
        jdbi.useExtension(ClanDao.class,
                dao -> {

                dao.clearClans(clans);
                dao.updateClans(clans);
                }
        );
        ArrayList<MemberEntry> members = new ArrayList<>();
        for(Clan clan : clans) {
            members.ensureCapacity(clan.getMembers().size());
            for(ClanMember member : clan.getMembers())
                members.add(new MemberEntry(clan, member));
        }
        jdbi.useHandle(handle -> {
            handle.createUpdate("DELETE * FROM members WHERE `clan` NOT IN (<ignored>)")
                    .bindList("ignored", clans.stream().map(Clan::getTag).toList()).execute();
            handle.createUpdate("INSERT INTO members (`uuid`, `name`, `clan`, `role`) VALUES (<values>) " +
                    "ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `clan`=VALUES(`clan`), `role`=VALUES(`role`)")
                    .bindMethodsList("values", members, CLAN_MEMBER_METHODS).execute();
        });
    }

}
