package com.manya.clans.hook;

import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.Clan;
import com.manya.clans.manager.ClanManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    public static final String IDENTIFIER = "decaliumclans";
    public static final String CLAN_DISPLAY_NAME = "clan_display_name",
            CLAN_TAG = "clan_tag";
    private final ClanManager clanManager;

    public ClanPlaceholderExpansion(ClanManager clanManager) {

        this.clanManager = clanManager;
    }
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getAuthor() {
        return DecaliumClans.AUTHOR;
    }

    @Override
    public String getVersion() {
        return DecaliumClans.VERSION;
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {

        Clan clan = clanManager.getUserClan(p.getUniqueId());
        if(clan == null) return "";
        switch (params) {
            case CLAN_TAG:
                return clan.getTag();
            case CLAN_DISPLAY_NAME:
                return LegacyComponentSerializer.legacySection().serialize(clan.getDisplayName());
        }
        return "";
    }
}
