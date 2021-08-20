package org.gepron1x.clans.event.member;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.event.clan.ClanEvent;
import org.jetbrains.annotations.NotNull;


public class ClanAddMemberEvent extends ClanEvent implements Cancellable, MemberEvent {

    private static final HandlerList handlers = new HandlerList();
    private final ClanMember member;

    private boolean cancelled = false;

    public ClanAddMemberEvent(@NotNull Clan clan, @NotNull ClanMember member) {
        super(clan);
        this.member = member;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public ClanMember getMember() {
        return member;
    }
}
