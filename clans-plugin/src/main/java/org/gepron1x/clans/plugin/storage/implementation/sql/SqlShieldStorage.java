/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.plugin.storage.ShieldStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ShieldRowMapper;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Jdbi;

public final class SqlShieldStorage implements ShieldStorage {

    private static final ShieldRowMapper MAPPER = new ShieldRowMapper();
    @Language("SQL")
    private static final String INSERT_OR_UPDATE_SHIELD = "INSERT INTO `shields` (`clan_id`, `start`, `end`) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE `start`=VALUES(`start`), `end`=VALUES(`end`)";

    @Language("SQL")
    private static final String DELETE_SHIELD = "DELETE FROM `shields` WHERE `clan_id`=(SELECT id FROM clans WHERE tag=?)";

    @Language("SQL")
    private static final String GET_SHIELD = "SELECT * FROM `shields` WHERE `clan_id`=(SELECT id FROM clans WHERE tag=?)";


    private final Jdbi jdbi;

    public SqlShieldStorage(Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    @Override
    public void add(int clanId, Shield shield) {
        jdbi.useHandle(handle -> handle.createUpdate(INSERT_OR_UPDATE_SHIELD)
                .bind(0, clanId)
                .bind(1, shield.started())
                .bind(2, shield.end())
        );

    }

    @Override
    public void remove(String tag) {
        jdbi.useHandle(handle -> handle.execute(DELETE_SHIELD, tag));
    }

    @Override
    public Shield get(String tag) {
        return jdbi.withHandle(handle -> handle.createQuery(GET_SHIELD).map(MAPPER).findOne()).orElse(Shield.NONE);
    }
}
