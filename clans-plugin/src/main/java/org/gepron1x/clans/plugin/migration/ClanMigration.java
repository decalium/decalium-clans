package org.gepron1x.clans.plugin.migration;

import com.destroystokyo.paper.util.SneakyThrow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;

public final class ClanMigration {


    private static final Type CLANS_SET_TYPE = TypeToken.getParameterized(Set.class, Clan.class).getType();

    private final Logger logger;
    private final ClanRepository manager;
    private final File file;
    private final Gson gson;

    public ClanMigration(@NotNull Logger logger, @NotNull ClanRepository manager, @NotNull File file, @NotNull Gson gson) {
        this.logger = logger;

        this.manager = manager;
        this.file = file;
        this.gson = gson;
    }

    public CentralisedFuture<Boolean> migrate() {

        logger.info("Started migration - saving clans to the {} file", file);

        return manager.clans().thenApply(clans -> {
            try {
                gson.toJson(clans, CLANS_SET_TYPE, new FileWriter(file));
                return true;
            } catch (IOException e) {
                SneakyThrow.sneaky(e);
                return false;
            }
        }).exceptionally(t -> {
            logger.error("Error happened while migrating:", t);
            return false;
        }).thenApply(success -> {
            if(success) {
                logger.info("Migration done successfully.");
            }
            return success;
    });
    }
}
