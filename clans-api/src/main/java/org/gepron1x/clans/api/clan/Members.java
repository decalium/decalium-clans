package org.gepron1x.clans.api.clan;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Members extends Iterable<ClanMember> {

    @NotNull ClanMember owner();
    @NotNull Set<ClanMember> members();

    Optional<ClanMember> member(@NotNull UUID uniqueId);

    default Optional<ClanMember> member(@NotNull OfflinePlayer player) {
        return member(player.getUniqueId());
    }



}
