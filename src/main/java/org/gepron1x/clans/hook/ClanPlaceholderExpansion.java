package org.gepron1x.clans.hook;

import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.Clan;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.manager.ClanManager;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    public static final String IDENTIFIER = "decaliumclans";
    public static final String CLAN_DISPLAY_NAME = "clan_display_name",
            CLAN_TAG = "clan_tag";
    private final ClanManager manager;


    public ClanPlaceholderExpansion(ClanManager manager) {

        this.manager = manager;
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

        Clan clan = manager.getUserClan(p);
        if(clan == null) return "";
        return switch (params) {
            case CLAN_TAG -> clan.getTag();
            case CLAN_DISPLAY_NAME -> LegacyComponentSerializer.legacySection().serialize(clan.getDisplayName());
            default -> "";
        };
    }
}
