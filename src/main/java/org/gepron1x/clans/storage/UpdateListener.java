package org.gepron1x.clans.storage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.event.clan.ClanCreatedEvent;
import org.gepron1x.clans.event.clan.ClanDeletedEvent;
import org.gepron1x.clans.event.home.ClanAddHomeEvent;
import org.gepron1x.clans.event.home.ClanRemoveHomeEvent;
import org.gepron1x.clans.event.member.ClanAddMemberEvent;
import org.gepron1x.clans.event.member.ClanRemoveMemberEvent;
import org.gepron1x.clans.storage.dao.ClanDao;
import org.gepron1x.clans.storage.dao.ClanHomeDao;
import org.gepron1x.clans.storage.dao.ClanMemberDao;
import org.gepron1x.clans.storage.property.PropertyUpdateEvent;
import org.gepron1x.clans.storage.task.DatabaseUpdate;
import org.gepron1x.clans.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

public class UpdateListener implements Listener {
    private final Map<Class<?>, TableProperties<?>> tableProperties = CollectionUtils.toMap(TableProperties::target,
            new TableProperties<>(Clan.class, "clans", "tag", Clan::getTag),
            new TableProperties<>(ClanMember.class, "members", "uuid", ClanMember::getUniqueId),
            new TableProperties<>(ClanHome.class, "homes", "name", ClanHome::getName)
    );
    private final Queue<DatabaseUpdate> updates = new ArrayDeque<>();

    private record TableProperties<T>(Class<T> target, String name, String primaryKey, Function<T, Object> keyMapper) {
        public Object getKey(Object object) {
            return keyMapper.apply(target.cast(object));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanCreatedEvent event) {
        updates.add(jdbi -> jdbi.useExtension(ClanDao.class, dao -> dao.addClan(event.getClan())));
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanDeletedEvent event) {

        Clan clan = event.getClan();
        updates.add(jdbi -> {
            jdbi.useExtension(ClanDao.class, dao -> dao.removeClan(clan));
            jdbi.useExtension(ClanMemberDao.class, dao -> dao.clearMembers(clan));
            jdbi.useExtension(ClanHomeDao.class, dao -> dao.removeHomes(clan));
        });
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanAddMemberEvent event) {
        updates.add(jdbi -> jdbi.useExtension(ClanMemberDao.class,
                dao -> dao.addMember(event.getMember(), event.getClan()))
        );
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanRemoveMemberEvent event) {
        updates.add(jdbi -> jdbi.useExtension(ClanMemberDao.class, dao -> dao.removeMember(event.getMember())));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanAddHomeEvent event) {
        updates.add(jdbi -> jdbi.useExtension(ClanHomeDao.class, dao -> dao.addHome(event.getClan(), event.getHome())));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(ClanRemoveHomeEvent event) {
        updates.add(jdbi -> jdbi.useExtension(ClanHomeDao.class, dao -> dao.removeHome(event.getHome())));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(PropertyUpdateEvent event) {

        TableProperties<?> tableProperties = this.tableProperties.get(event.getProperty().getTargetType());
        if(tableProperties == null) return;
        String query = MessageFormat.format("UPDATE {0} SET `{1}`=:value WHERE `{2}`=:key",
                tableProperties.name(), event.getProperty().getName(), tableProperties.primaryKey());

        updates.add(jdbi ->
                jdbi.useHandle(handle -> handle.createUpdate(query).bind("value", event.getValue())
                .bind("key", tableProperties.getKey(event.getValue())).execute())
        );
    }


    public Queue<DatabaseUpdate> getUpdates() {
        return updates;
    }
    public void clearUpdates() {
        updates.clear();
    }

}
