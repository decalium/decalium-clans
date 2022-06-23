package org.gepron1x.clans.plugin.util.player;

import org.bukkit.Server;
import org.bukkit.entity.Player;

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
}
