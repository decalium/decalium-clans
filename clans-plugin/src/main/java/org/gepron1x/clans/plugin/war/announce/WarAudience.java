package org.gepron1x.clans.plugin.war.announce;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import org.gepron1x.clans.api.chat.GroupAudience;
import org.gepron1x.clans.api.war.War;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class WarAudience implements GroupAudience {
    private final War war;

    public WarAudience(War war) {

        this.war = war;
    }
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return war.teams();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarAudience that = (WarAudience) o;
        return war.equals(that.war);
    }

	@Override
    public int hashCode() {
        return Objects.hash(war);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("war", war)
                .toString();
    }
}
