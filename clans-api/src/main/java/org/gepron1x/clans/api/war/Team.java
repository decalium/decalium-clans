package org.gepron1x.clans.api.war;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Team extends ForwardingAudience {

    ClanReference clan();

    Collection<PlayerReference> members();

    default boolean isMember(Player player) {
        for(PlayerReference ref : members()) {
            if(ref.player().map(p -> p.equals(player)).orElse(false)) return true;
        }
        return false;
    }

    Collection<PlayerReference> alive();

    boolean onDeath(Player player);

    boolean isAlive();

    @Override
    @NotNull default Iterable<? extends Audience> audiences() {
        return this.members();
    }
}
