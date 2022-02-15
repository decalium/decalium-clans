package org.gepron1x.clans.plugin.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanPlaceholderResolver(@NotNull Clan clan) implements TagResolver.WithoutArguments {

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
    public @Nullable Tag resolve(@NotNull String name) {

        Component component = switch (name) {
            case ID -> Component.text(clan.getId());
            case TAG -> Component.text(clan.getTag());
            case DISPLAY_NAME -> clan.getDisplayName();
            case MEMBERS_SIZE -> Component.text(clan.getMembers().size());
            case HOMES_SIZE -> Component.text(clan.getHomes().size());
            default -> null;
        };

        if(component != null) return Tag.inserting(component);

        if(name.startsWith(STATISTIC)) {
            StatisticType type = new StatisticType(name.substring(STATISTIC.length()));
            int value = clan.getStatisticOr(type, 0);
            return Tag.inserting(Component.text(value));
        }

        if(name.startsWith(OWNER)) {
            ClanMember member = clan.getOwner();
            return PrefixedTagResolver.prefixed(new ClanMemberPlaceholderResolver(member), "owner").resolve(name);
        }

        return null;


    }
}
