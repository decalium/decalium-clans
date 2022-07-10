package org.gepron1x.clans.plugin.util.player;

import com.google.common.base.MoreObjects;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public final class UuidPlayerReference implements PlayerReference {

    private final Server server;
    private final UUID uniqueId;

    public UuidPlayerReference(Server server, UUID uniqueId) {

        this.server = server;
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean ifOnline(Consumer<Player> consumer) {
        Optional<Player> opt = player();
        opt.ifPresent(consumer);
        return opt.isPresent();
    }

    @Override
    public Optional<Player> player() {
        return Optional.ofNullable(this.server.getPlayer(this.uniqueId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UuidPlayerReference that = (UuidPlayerReference) o;
        return server.equals(that.server) && uniqueId.equals(that.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, uniqueId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("server", server)
                .add("uniqueId", uniqueId)
                .toString();
    }
}
