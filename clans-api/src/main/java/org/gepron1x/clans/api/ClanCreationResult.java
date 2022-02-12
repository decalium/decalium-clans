package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

public record ClanCreationResult(@Nullable Clan clan, @NotNull Status status) {

    private static final ClanCreationResult ALREADY_EXISTS = failure(Status.ALREADY_EXISTS);
    private static final ClanCreationResult MEMBERS_IN_OTHER_CLANS = failure(Status.MEMBERS_IN_OTHER_CLANS);

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

    public void ifSuccess(Consumer<Clan> consumer) {
        if(clan != null) consumer.accept(clan);
    }

    public Clan orElseThrow() {
        if(clan == null) throw new NoSuchElementException("no clan present");
        return clan;
    }

    public Optional<Clan> asOptional() {
        return Optional.ofNullable(clan);
    }

    public static ClanCreationResult failure(@NotNull Status status) {
        return new ClanCreationResult(null, status);
    }

    public static ClanCreationResult alreadyExists() {
        return ALREADY_EXISTS;
    }

    public static ClanCreationResult membersInOtherClans() {
        return MEMBERS_IN_OTHER_CLANS;
    }
}
