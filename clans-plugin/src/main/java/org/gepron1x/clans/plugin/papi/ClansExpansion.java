package org.gepron1x.clans.plugin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanCache;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class ClansExpansion extends PlaceholderExpansion {

    private static final String TAG = "tag", DISPLAY_NAME = "display_name", OWNER = "owner", MEMBER_COUNT = "member_count", MEMBER_ROLE = "member_role";


    private final Server server;
    private final ClanCache cache;
    private final LegacyComponentSerializer legacy;

    public ClansExpansion(@NotNull Server server, @NotNull ClanCache cache, @NotNull LegacyComponentSerializer legacy) {
        this.server = server;
        this.cache = cache;
        this.legacy = legacy;
    }


    @Override
    public String getIdentifier() {
        return "clans";
    }

    @Override
    public String getAuthor() {
        return "gepron1x";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        Clan clan = cache.getUserClan(p.getUniqueId());
        if(clan == null) return "";
        ClanMember member = Objects.requireNonNull(clan.getMember(p));
        return switch(params) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
            case TAG -> clan.getTag();
            case DISPLAY_NAME -> legacy.serialize(clan.getDisplayName());
            case OWNER -> clan.getOwner().asOffline(server).getName();
            case MEMBER_COUNT -> String.valueOf(clan.getMembers().size());
            case MEMBER_ROLE -> legacy.serialize(member.getRole().getDisplayName());
            default -> null;
        };
    }



}
