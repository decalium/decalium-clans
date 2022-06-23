package org.gepron1x.clans.plugin.util.player;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;

public interface PlayerReference {

    boolean ifOnline(Consumer<Player> consumer);

    Optional<Player> player();


}
