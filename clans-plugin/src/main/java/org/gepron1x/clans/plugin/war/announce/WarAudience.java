package org.gepron1x.clans.plugin.war.announce;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.gepron1x.clans.plugin.war.War;
import org.jetbrains.annotations.NotNull;

public final class WarAudience implements ForwardingAudience {
    private final War war;

    public WarAudience(War war) {

        this.war = war;
    }
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return war.teams();
    }
}
