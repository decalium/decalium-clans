package org.gepron1x.clans.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.config.MiniComponent;
import org.gepron1x.clans.ClanManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClanChatRenderer implements ChatRenderer {
    private final ClanManager manager;
    private final MiniComponent format;

    public ClanChatRenderer(ClanManager manager, MiniComponent format) {
        this.manager = manager;
        this.format = format;
    }
    @Override
    public @NotNull Component render(@NotNull Player source,
                                     @NotNull Component sourceDisplayName,
                                     @NotNull Component message,
                                     @NotNull Audience viewer) {
        Clan clan = Objects.requireNonNull(manager.getUserClan(source));
        ClanMember member = clan.getMember(source);


        return format.parse(source,
                "clan", clan.getDisplayName(),
                "player", sourceDisplayName,
                "role", member.getRole().getDisplayName(),
                "message", message);
    }
}
