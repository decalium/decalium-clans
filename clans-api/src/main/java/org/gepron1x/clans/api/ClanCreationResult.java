package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanCreationResult(@Nullable Clan clan, @NotNull Status status) {

    public enum Status {
        SUCCESS,
        ALREADY_EXISTS,
        MEMBERS_IN_OTHER_CLANS;

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }

    public boolean isSuccess() {
        return clan != null;
    }

    public static ClanCreationResult success(@NotNull Clan clan) {
        return new ClanCreationResult(clan, Status.SUCCESS);
    }

    public static ClanCreationResult failure(@NotNull Status status) {
        return new ClanCreationResult(null, status);
    }

    public static ClanCreationResult alreadyExists() {
        return failure(Status.ALREADY_EXISTS);
    }

    public static ClanCreationResult membersInOtherClans() {
        return failure(Status.MEMBERS_IN_OTHER_CLANS);
    }
}
