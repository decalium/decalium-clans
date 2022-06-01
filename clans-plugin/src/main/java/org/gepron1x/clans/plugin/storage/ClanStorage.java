package org.gepron1x.clans.plugin.storage;

import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanStorage {


    void initialize();
    void shutdown();

    @Nullable IdentifiedDraftClan loadClan(@NotNull String tag);

    @Nullable IdentifiedDraftClan loadClan(int id);
    @Nullable IdentifiedDraftClan loadUserClan(@NotNull UUID uuid);

    OptionalInt lookupId(@NotNull String tag);
    OptionalInt lookupId(@NotNull UUID member);
    Set<Integer> clanIds();

    @NotNull Set<IdentifiedDraftClanImpl> loadClans();

    SaveResult saveClan(@NotNull DraftClan clan);

    void applyEdition(int id, @NotNull Consumer<ClanEdition> consumer);

    boolean removeClan(int id);

    record SaveResult(int id, ClanCreationResult.Status status) {
        public static final SaveResult ALREADY_EXISTS = new SaveResult(Integer.MIN_VALUE, ClanCreationResult.Status.ALREADY_EXISTS);
        public static final SaveResult MEMBERS_IN_OTHER_CLANS = new SaveResult(Integer.MIN_VALUE, ClanCreationResult.Status.MEMBERS_IN_OTHER_CLANS);

        public static SaveResult success(int id) {
            return new SaveResult(id, ClanCreationResult.Status.SUCCESS);
        }





    }


}
