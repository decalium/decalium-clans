package org.gepron1x.clans.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.config.ClansConfig;
import org.gepron1x.clans.storage.argument.ComponentArgumentFactory;
import org.gepron1x.clans.storage.argument.ItemStackArgumentFactory;
import org.gepron1x.clans.storage.argument.LocationArgumentFactory;
import org.gepron1x.clans.storage.argument.UuidArgumentFactory;
import org.gepron1x.clans.storage.mappers.row.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.UUID;

public final class StorageCreator {
    private final DecaliumClans plugin;

    public StorageCreator(DecaliumClans plugin) {
        this.plugin = plugin;
    }

    public Storage create() {

        HikariConfig config = new HikariConfig();
        ClansConfig.Storage storageConfig = plugin.getClansConfig().storage();


        setupConnection(config, storageConfig);
        setupPooling(config, storageConfig);
        Jdbi jdbi = Jdbi.create(new HikariDataSource(config));
        jdbi.installPlugin(new SqlObjectPlugin());


        registerRowMappers(jdbi);
        registerColumnMappers(jdbi);
        registerArguments(jdbi);

        return new Storage(plugin, storageConfig.storageType(), jdbi, storageConfig.saveTaskPeriod());
    }

    private void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(ClanBuilder.class, new ClanMapper(plugin))
                .registerRowMapper(ClanMember.class, new ClanMemberMapper(plugin.getRoleRegistry()))
                .registerRowMapper(StatisticRow.class, new StatisticRowMapper(plugin.getStatisticTypeRegistry()))
                .registerRowMapper(ClanHome.class, new ClanHomeMapper());
    }

    private void registerColumnMappers(Jdbi jdbi) {
        jdbi.registerColumnMapper(UUID.class, Mappers.UUID)
                .registerColumnMapper(Component.class, Mappers.COMPONENT)
                .registerColumnMapper(ItemStack.class, Mappers.ITEM_STACK)
                .registerColumnMapper(Location.class, Mappers.LOCATION);
    }

    private void registerArguments(Jdbi jdbi) {
        jdbi.registerArgument(new UuidArgumentFactory())
                .registerArgument(new ComponentArgumentFactory())
                .registerArgument(new ItemStackArgumentFactory())
                .registerArgument(new LocationArgumentFactory());
    }

    private void setupPooling(HikariConfig hikariConfig, ClansConfig.Storage storageSettings) {

        ClansConfig.Storage.HikariPool hikariSettings = storageSettings.hikariPool();
        hikariConfig.setPoolName(hikariSettings.poolName());
        hikariConfig.setMaximumPoolSize(hikariSettings.maxPoolSize());
        hikariConfig.setMinimumIdle(hikariSettings.maximumIdle());
        hikariConfig.setMaxLifetime(hikariSettings.maxLifeTime());
        hikariConfig.setConnectionTimeout(hikariSettings.connectionTimeOut());
        hikariConfig.setInitializationFailTimeout(-1);

    }

    private void setupConnection(HikariConfig hikariConfig, ClansConfig.Storage storageSettings) {
        StorageType type = storageSettings.storageType();
        String url, password, user;
        if (type == StorageType.MYSQL) {
            ClansConfig.Storage.AuthDetails details = storageSettings.authDetails();
            url = MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL={2}",
                    details.host(), details.database(), details.useSSL());
            password = details.password();
            user = details.user();
        } else {

            Path path = plugin.getDataFolder().toPath().resolve("clans");
            url = MessageFormat.format("jdbc:h2:file:.{0};mode=MySQL", path);

            password = "";
            user = "sa";
        }
        hikariConfig.setDriverClassName(type.getDriverName());
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(user);
    }



}
