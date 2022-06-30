package org.gepron1x.clans.plugin.util.player;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public interface PlayerReference extends ForwardingAudience.Single {

    boolean ifOnline(Consumer<Player> consumer);

    Optional<Player> player();

    @Override
    @NotNull default Audience audience() {
        return player().isEmpty() ? Audience.empty() : player().get();
    }
}
