package org.gepron1x.clans.plugin.announce;

import org.bukkit.Server;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.plugin.AdaptingClanRepository;
import org.gepron1x.clans.plugin.config.MessagesConfig;


public final class AnnouncingClanRepository extends AdaptingClanRepository {
    public AnnouncingClanRepository(ClanRepository repository, Server server, MessagesConfig messages) {
        super(repository, clan -> new AnnouncingClan(clan, messages, server));
    }
}
