package org.gepron1x.clans.api.audience;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.pointer.Pointers;
import org.bukkit.Server;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.util.ClanOnlinePlayers;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ClanAudience implements ForwardingAudience {


    private final DraftClan clan;
    private final Server server;

    public ClanAudience(@NotNull DraftClan clan, @NotNull Server server) {
        this.clan = clan;
        this.server = server;
    }


    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return new ClanOnlinePlayers(this.clan, this.server);
    }

    @Override
    public @NotNull Pointers pointers() {
        return Pointers.builder()
                .withStatic(Identity.DISPLAY_NAME, this.clan.displayName())
                .withStatic(Identity.NAME, this.clan.tag()).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanAudience that = (ClanAudience) o;
        return clan.equals(that.clan) && server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan, server);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clan", clan)
                .add("server", server)
                .toString();
    }
}
