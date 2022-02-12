package org.gepron1x.clans.plugin.chat;

import com.google.common.base.Strings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.bukkit.Bukkit;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanMemberPlaceholderResolver(@NotNull ClanMember member) implements PlaceholderResolver {
    private static final String ROLE = "role";
    private static final String NAME = "name";
    private static final String UUID = "uuid";

    public static ClanMemberPlaceholderResolver clanMember(@NotNull ClanMember member) {
        return new ClanMemberPlaceholderResolver(member);
    }
    @Override
    public @Nullable Replacement<?> resolve(@NotNull String key) {

        return switch (key) {
            case ROLE -> Replacement.component(member.getRole().getDisplayName());
            case NAME -> Replacement.component(Component.text(Strings.nullToEmpty(member.asOffline(Bukkit.getServer()).getName())));
            case UUID -> Replacement.component(Component.text(member.getUniqueId().toString()));
            default -> null;
        };
    }
}
