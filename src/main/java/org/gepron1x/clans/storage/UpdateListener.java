package org.gepron1x.clans.storage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.events.clan.*;
import org.gepron1x.clans.events.member.MemberSetRoleEvent;
import org.gepron1x.clans.storage.task.DatabaseUpdate;

import java.util.ArrayDeque;
import java.util.Queue;

public class UpdateListener implements Listener {
    private final Queue<DatabaseUpdate> updates = new ArrayDeque<>();
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanCreatedEvent event) {
        if(event.isCancelled()) return;
        updates.add(jdbi -> jdbi.useExtension(ClanDao.class, dao -> dao.insertClan(event.getClan())));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanDeletedEvent event) {
        if(event.isCancelled()) return;
        updates.add(jdbi -> {
            Clan clan = event.getClan();
            jdbi.useExtension(ClanDao.class, dao -> dao.removeClan(clan));
            jdbi.useExtension(ClanMemberDao.class, dao -> dao.clearMembers(clan));
        });
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanAddMemberEvent event) {
        if(event.isCancelled()) return;
        updates.add(jdbi -> jdbi.useExtension(ClanMemberDao.class,
                dao -> dao.addMember(event.getMember(), event.getClan()))
        );
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanRemoveMemberEvent event) {
        if (event.isCancelled()) return;
        updates.add(jdbi -> jdbi.useExtension(ClanMemberDao.class, dao -> dao.removeMember(event.getMember())));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanSetDisplayNameEvent event) {
        if(event.isCancelled()) return;
        updates.add(jdbi ->
                jdbi.useExtension(ClanDao.class,
                        dao -> dao.setDisplayName(event.getClan(), event.getNewDisplayName()))
        );
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(MemberSetRoleEvent event) {
        if(event.isCancelled()) return;
        updates.add(jdbi -> {
            jdbi.useExtension(ClanMemberDao.class, dao -> dao.setRole(event.getMember(), event.getNewRole()));
        });
    }

    public Queue<DatabaseUpdate> getUpdates() {
        return updates;
    }
    public void clearUpdates() {
        updates.clear();
    }

}
