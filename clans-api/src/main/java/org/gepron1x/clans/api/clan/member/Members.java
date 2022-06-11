package org.gepron1x.clans.api.clan.member;

import org.bukkit.OfflinePlayer;
import org.gepron1x.clans.api.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface Members extends Registry<UUID, ClanMember> {

    @NotNull ClanMember owner();

    default Optional<ClanMember> value(@NotNull OfflinePlayer player) {
        return value(player.getUniqueId());
    }


}
