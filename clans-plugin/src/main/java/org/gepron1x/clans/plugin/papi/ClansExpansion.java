package org.gepron1x.clans.plugin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.ClanCacheImpl;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.storage.IdentifiedDraftClanImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public final class ClansExpansion extends PlaceholderExpansion {

    private static final String TAG = "tag", DISPLAY_NAME = "display_name", OWNER = "owner", MEMBER_COUNT = "member_count", MEMBER_ROLE = "member_role";


    private final Server server;
    private final ClansConfig clansConfig;
    private final ClanCacheImpl cache;
    private final LegacyComponentSerializer legacy;

    public ClansExpansion(@NotNull Server server, @NotNull ClansConfig clansConfig, @NotNull ClanCacheImpl cache, @NotNull LegacyComponentSerializer legacy) {
        this.server = server;
        this.clansConfig = clansConfig;
        this.cache = cache;
        this.legacy = legacy;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "clans";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gepron1x";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        IdentifiedDraftClanImpl identifiedDraftClan = cache.getUserClan(p.getUniqueId());
        if(identifiedDraftClan == null) return legacy.serialize(clansConfig.noClanPlaceholder());

        DraftClan clan = identifiedDraftClan.clan();

        ClanMember member = Objects.requireNonNull(clan.member(p));
        return switch(params) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
            case TAG -> clan.tag();
            case DISPLAY_NAME -> legacy.serialize(clan.displayName());
            case OWNER -> legacy.serialize(clan.owner().renderName(server));
            case MEMBER_COUNT -> String.valueOf(clan.members().size());
            case MEMBER_ROLE -> legacy.serialize(member.role().displayName());
            default -> null;
        };
    }



}
