package org.gepron1x.clans.plugin.migration;

import com.google.gson.Gson;
import org.gepron1x.clans.api.ClanManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class ClanMigration {

    private final ClanManager manager;
    private final File file;
    private final Gson gson;

    public ClanMigration(@NotNull ClanManager manager, @NotNull File file, @NotNull Gson gson) {

        this.manager = manager;
        this.file = file;
        this.gson = gson;
    }

}
