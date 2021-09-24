package org.gepron1x.clans.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.config.Message;
import org.gepron1x.clans.ClanManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClanChatRenderer implements ChatRenderer {
    private final ClanManager manager;
    private final Message format;

    public ClanChatRenderer(ClanManager manager, Message format) {
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



        return format.with("clan_name", clan.getDisplayName())
                .with("clan_tag", clan.getTag()).asComponent();
    }
}
