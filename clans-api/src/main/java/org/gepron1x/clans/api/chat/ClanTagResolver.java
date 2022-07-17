package org.gepron1x.clans.api.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanTagResolver(@NotNull DraftClan clan) implements TagResolver.WithoutArguments {

    public static ClanTagResolver clan(@NotNull DraftClan clan) {
        return new ClanTagResolver(clan);
    }

    public static TagResolver prefixed(@NotNull DraftClan clan, String prefix) {
        return PrefixedTagResolver.prefixed(clan(clan), prefix);
    }

    public static TagResolver prefixed(@NotNull DraftClan clan) {
        return prefixed(clan, "clan");
    }

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
            case TAG -> Component.text(clan.tag());
            case DISPLAY_NAME -> clan.displayName();
            case MEMBERS_SIZE -> Component.text(clan.members().size());
            case HOMES_SIZE -> Component.text(clan.homes().size());
            default -> null;
        };

        if(component != null) return Tag.inserting(component);

        if(name.startsWith(STATISTIC)) {
            StatisticType type = new StatisticType(name.substring(STATISTIC.length()));
            int value = clan.statisticOr(type, 0);
            return Tag.inserting(Component.text(value));
        }

        if(name.startsWith(OWNER)) {
            ClanMember member = clan.owner();
            return PrefixedTagResolver.prefixed(new ClanMemberTagResolver(member), "owner").resolve(name);
        }

        return null;
    }
}
