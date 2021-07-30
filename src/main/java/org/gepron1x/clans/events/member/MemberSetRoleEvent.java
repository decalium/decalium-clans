package org.gepron1x.clans.events.member;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.role.ClanRole;
import org.jetbrains.annotations.NotNull;

public class MemberSetRoleEvent extends MemberEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private ClanRole newRole;
    private boolean cancelled;
    public MemberSetRoleEvent(@NotNull ClanMember member, @NotNull ClanRole newRole) {
        super(member);
        this.newRole = newRole;
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
    @NotNull
    public ClanRole getNewRole() {
        return newRole;
    }

    public void setNewRole(@NotNull ClanRole newRole) {
        this.newRole = newRole;
    }
}
