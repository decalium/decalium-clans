package org.gepron1x.clans.storage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.events.PropertyUpdateEvent;
import org.gepron1x.clans.events.clan.*;
import org.gepron1x.clans.events.member.ClanAddMemberEvent;
import org.gepron1x.clans.events.member.ClanRemoveMemberEvent;
import org.gepron1x.clans.storage.dao.ClanDao;
import org.gepron1x.clans.storage.dao.ClanHomeDao;
import org.gepron1x.clans.storage.dao.ClanMemberDao;
import org.gepron1x.clans.storage.dao.PropertyDao;
import org.gepron1x.clans.storage.task.DatabaseUpdate;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

public class UpdateListener implements Listener {
    private final Map<Class<?>, Class<? extends PropertyDao>> propertyDaoMap =
            Map.of(ClanMember.class, ClanMemberDao.class, ClanHome.class, ClanHomeDao.class, Clan.class, ClanDao.class);
    private final Queue<DatabaseUpdate> updates = new ArrayDeque<>();
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanCreatedEvent event) {
        if(event.isCancelled()) return;
        updates.add(jdbi -> jdbi.useExtension(ClanDao.class, dao -> dao.addClan(event.getClan())));
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ClanDeletedEvent event) {
        if(event.isCancelled()) return;
        Clan clan = event.getClan();
        updates.add(jdbi -> {
            jdbi.useExtension(ClanDao.class, dao -> dao.removeClan(clan));
            jdbi.useExtension(ClanMemberDao.class, dao -> dao.clearMembers(clan));
            jdbi.useExtension(ClanHomeDao.class, dao -> dao.removeHomes(clan));
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
    public void on(PropertyUpdateEvent event) {
        if(event.isCancelled()) return;
        DatabaseUpdate update;
        Class<? extends PropertyDao> daoClass = propertyDaoMap.get(event.getProperty().getTargetType());
        if(daoClass == null) return;
        updates.add(jdbi -> {
            jdbi.useExtension(daoClass, dao -> dao.updateProperty(event.getProperty(), event.getTarget(), event.getValue()));
        });
    }

    public Queue<DatabaseUpdate> getUpdates() {
        return updates;
    }
    public void clearUpdates() {
        updates.clear();
    }

}
