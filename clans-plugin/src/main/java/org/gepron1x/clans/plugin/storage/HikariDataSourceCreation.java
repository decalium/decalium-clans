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
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

import java.nio.file.Path;
import java.text.MessageFormat;

public final class HikariDataSourceCreation {
    private final Plugin plugin;
    private final ClansConfig clansConfig;

    public HikariDataSourceCreation(Plugin plugin, ClansConfig clansConfig) {
        this.plugin = plugin;
        this.clansConfig = clansConfig;
    }

    public HikariDataSource create() {
        HikariConfig config = new HikariConfig();
        setupConnection(config);
        setupPooling(config);
        return new HikariDataSource(config);
    }


    private void setupConnection(HikariConfig config) {
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
        config.setDriverClassName(type.driverClassName());
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
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
}
