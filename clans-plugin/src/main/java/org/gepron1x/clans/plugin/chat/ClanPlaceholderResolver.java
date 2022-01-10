package org.gepron1x.clans.plugin.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanPlaceholderResolver(@NotNull Clan clan) implements PlaceholderResolver {

    public static ClanPlaceholderResolver clan(@NotNull Clan clan) {
        return new ClanPlaceholderResolver(clan);
    }

    private static final String ID = "id";
    private static final String TAG = "tag";
    private static final String DISPLAY_NAME = "display_name";
    private static final String MEMBERS_SIZE = "members_size";
    private static final String HOMES_SIZE = "homes_size";

    private static final String STATISTIC = "statistic_";
    private static final String MEMBER = "member_";
    private static final String OWNER = "owner_";

    @Override
    public @Nullable Replacement<?> resolve(@NotNull String key) {

        Replacement<?> replacement = switch (key) {
            case ID -> Replacement.component(Component.text(clan.getId()));
            case TAG -> Replacement.component(Component.text(clan.getTag()));
            case DISPLAY_NAME -> Replacement.component(clan.getDisplayName());
            case MEMBERS_SIZE -> Replacement.component(Component.text(clan.getMembers().size()));
            case HOMES_SIZE -> Replacement.component(Component.text(clan.getHomes().size()));
            default -> null;
        };

        if(replacement != null) return replacement;

        if(key.startsWith(STATISTIC)) {
            StatisticType type = new StatisticType(key.substring(STATISTIC.length()));
            int value = clan.getStatisticOr(type, 0);
            return Replacement.component(Component.text(value));
        }

        if(key.startsWith(OWNER)) {
            ClanMember member = clan.getOwner();
            return PrefixedPlaceholderResolver.prefixed(new ClanMemberPlaceholderResolver(member), "owner").resolve(key);
        }

        return null;


    }
}
