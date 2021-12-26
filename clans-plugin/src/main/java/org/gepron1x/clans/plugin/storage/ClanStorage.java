package org.gepron1x.clans.plugin.storage;

import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface ClanStorage {


    void initialize();

    @Nullable Clan loadClan(@NotNull String tag);
    @Nullable Clan loadUserClan(@NotNull UUID uuid);

    @NotNull Set<Clan> loadClans();

    ClanCreationResult saveClan(@NotNull DraftClan clan);

    void editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> editor);

    void removeClan(@NotNull Clan clan);


    boolean clanExists(@NotNull String tag);



}
