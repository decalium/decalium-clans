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
package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.gepron1x.clans.plugin.storage.implementation.sql.argument.Arguments;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column.ColumnMappers;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public final class JdbiCreation {

    private DataSource dataSource;

    public JdbiCreation(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Jdbi create() {
        Jdbi jdbi = Jdbi.create(dataSource);
        registerArguments(jdbi);
        registerColumnMappers(jdbi);
        return jdbi;
    }

    private void registerColumnMappers(Jdbi jdbi) {
        jdbi.registerColumnMapper(ColumnMappers.STATISTIC_TYPE)
                .registerColumnMapper(ColumnMappers.COMPONENT)
                .registerColumnMapper(ColumnMappers.UUID)
                .registerColumnMapper(ColumnMappers.ITEM_STACK)
                .registerColumnMapper(ColumnMappers.INSTANT);
    }

    private void registerArguments(Jdbi jdbi) {
        jdbi.registerArgument(Arguments.COMPONENT)
                .registerArgument(Arguments.STATISTIC_TYPE)
                .registerArgument(Arguments.CLAN_ROLE)
                .registerArgument(Arguments.UUID)
                .registerArgument(Arguments.ITEM_STACK)
                .registerArgument(Arguments.INSTANT);
    }
}
