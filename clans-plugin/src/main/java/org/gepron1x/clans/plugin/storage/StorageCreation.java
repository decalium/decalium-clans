/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.flywaydb.core.Flyway;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.storage.implementation.sql.SqlClanStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.argument.Arguments;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column.ColumnMappers;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.text.MessageFormat;

public final class StorageCreation {

    private final Plugin plugin;
    private final ClansConfig clansConfig;
    private final ClanBuilderFactory factory;
    private final RoleRegistry roleRegistry;

    public StorageCreation(@NotNull Plugin plugin,
                           @NotNull ClansConfig clansConfig,
                           @NotNull ClanBuilderFactory factory,
                           @NotNull RoleRegistry roleRegistry) {

        this.plugin = plugin;
        this.clansConfig = clansConfig;
        this.factory = factory;
        this.roleRegistry = roleRegistry;
    }


    public ClanStorage create() {

        HikariConfig config = new HikariConfig();
        setupConnection(config);
        setupPooling(config);
        HikariDataSource ds = new HikariDataSource(config);
        Flyway flyway = Flyway.configure(DecaliumClansPlugin.class.getClassLoader())
                .dataSource(ds)
                .baselineVersion("0")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load();
        flyway.migrate();
        Jdbi jdbi = Jdbi.create(ds);
        registerColumnMappers(jdbi);
        registerArguments(jdbi);
        
        return new SqlClanStorage(plugin, jdbi, ds, clansConfig.storage().type(), factory, roleRegistry);

    }

    private void setupPooling(HikariConfig hikariConfig) {
        ClansConfig.Storage.HikariPoolSettings poolSettings = clansConfig.storage().hikariPool();

        hikariConfig.setPoolName(poolSettings.poolName());
        hikariConfig.setMaximumPoolSize(poolSettings.maxPoolSize());
        hikariConfig.setMinimumIdle(poolSettings.maximumIdle());
        hikariConfig.setMaxLifetime(poolSettings.maxLifeTime());
        hikariConfig.setConnectionTimeout(poolSettings.connectionTimeout());
        hikariConfig.setInitializationFailTimeout(-1);

    }

    private void setupConnection(HikariConfig hikariConfig) {
        StorageType type = clansConfig.storage().type();
        ClansConfig.Storage.AuthDetails details = clansConfig.storage().authDetails();

        String url, username, password;

        if(type == StorageType.MYSQL) {
            url = MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL={2}", details.host(), details.database(), details.useSSL());
            username = details.username();
            password = details.password();
        } else if(type == StorageType.H2) {
            Path path = plugin.getDataFolder().toPath().resolve("clans");
            url = MessageFormat.format("jdbc:h2:file:./{0};mode=MySQL", path);
            username = "sa";
            password = "";
        } else {
            throw new UnsupportedOperationException("postrges/other databases are not supported yet.");
        }
        hikariConfig.setDriverClassName(type.driverClassName());
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);


    }

    private void registerColumnMappers(Jdbi jdbi) {
        jdbi.registerColumnMapper(ColumnMappers.STATISTIC_TYPE)
                .registerColumnMapper(ColumnMappers.COMPONENT)
                .registerColumnMapper(ColumnMappers.UUID)
                .registerColumnMapper(ColumnMappers.ITEM_STACK);
    }

    private void registerArguments(Jdbi jdbi) {
        jdbi.registerArgument(Arguments.COMPONENT)
                .registerArgument(Arguments.STATISTIC_TYPE)
                .registerArgument(Arguments.CLAN_ROLE)
                .registerArgument(Arguments.UUID)
                .registerArgument(Arguments.ITEM_STACK);
    }
}
