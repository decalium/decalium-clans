package org.gepron1x.clans.plugin.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanMemberPlaceholderResolver(@NotNull ClanMember member) implements TagResolver.WithoutArguments {
    private static final String ROLE = "role";
    private static final String NAME = "name";
    private static final String UUID = "uuid";

    public static ClanMemberPlaceholderResolver clanMember(@NotNull ClanMember member) {
        return new ClanMemberPlaceholderResolver(member);
    }
    @Override
    public @Nullable Tag resolve(@NotNull String name) {

        Component component = switch (name) {
            case ROLE -> member.getRole().asComponent();
            case NAME -> member.asComponent();
            case UUID -> Component.text(member.getUniqueId().toString());
            default -> null;
        };
        return component == null ? null : Tag.inserting(component);
    }
}
