package com.manya.clans.hook;

import com.manya.clans.DecaliumClans;
import com.manya.clans.clan.Clan;
import com.manya.clans.helper.ClanHelper;
import com.manya.clans.manager.ClanManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    public static final String IDENTIFIER = "decaliumclans";
    public static final String CLAN_DISPLAY_NAME = "clan_display_name",
            CLAN_TAG = "clan_tag";
    private final ClanHelper clanHelper;

    public ClanPlaceholderExpansion(ClanHelper clanHelper) {
        this.clanHelper = clanHelper;
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

        Clan clan = clanHelper.getUserClan(p);
        if(clan == null) return "";
        return switch (params) {
            case CLAN_TAG -> clan.getTag();
            case CLAN_DISPLAY_NAME -> LegacyComponentSerializer.legacySection().serialize(clan.getDisplayName());
            default -> "";
        };
    }
}
