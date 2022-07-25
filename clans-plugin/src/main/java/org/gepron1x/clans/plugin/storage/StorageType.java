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

import org.jdbi.v3.core.Handle;

import java.util.function.Consumer;

public enum StorageType {
    H2(org.h2.Driver.class.getName()),
    MYSQL("com.mysql.cj.jdbc.Driver"),
    POSTGRESQL("");

    private final String driverClassName;
    private final Consumer<Handle> onDisable;

    StorageType(String driverClassName, Consumer<Handle> onDisable) {
        this.driverClassName = driverClassName;
        this.onDisable = onDisable;
    }

    StorageType(String driverClassName) {
        this(driverClassName, h -> {});
    }

    public void disable(Handle handle) {
        this.onDisable.accept(handle);
    }

    public String driverClassName() {
        return driverClassName;
    }
}
