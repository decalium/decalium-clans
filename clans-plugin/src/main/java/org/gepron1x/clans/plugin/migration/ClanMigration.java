package org.gepron1x.clans.plugin.migration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Set;

public final class ClanMigration {


    private static final Type CLANS_SET_TYPE = TypeToken.getParameterized(Set.class, Clan.class).getType();

    private final ClanManager manager;
    private final File file;
    private final Gson gson;

    public ClanMigration(@NotNull ClanManager manager, @NotNull File file, @NotNull Gson gson) {

        this.manager = manager;
        this.file = file;
        this.gson = gson;
    }

    public CentralisedFuture<Boolean> migrate() {
        return null;
    }




}
